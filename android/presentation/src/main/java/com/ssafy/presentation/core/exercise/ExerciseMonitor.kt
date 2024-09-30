package com.ssafy.presentation.core.exercise

import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate.ActiveDurationCheckpoint
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.ssafy.presentation.core.exercise.data.ExerciseMetrics
import com.ssafy.presentation.core.exercise.data.ExerciseSessionData
import com.ssafy.presentation.core.exercise.manager.WearableClientManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseMonitor @Inject constructor(
    private val wearableClientManager: WearableClientManager,
    private val coroutineScope: CoroutineScope,
) : DataClient.OnDataChangedListener {
    val exerciseServiceState = MutableStateFlow(ExerciseServiceState())

    val exerciseSessionData = MutableStateFlow(ExerciseSessionData())

    private var isRunning = false

    fun run() {
        if (isRunning) return
        isRunning = true
        exerciseSessionData.update { ExerciseSessionData() }
        wearableClientManager.dataClient.addListener(this)
        collectExerciseMetrics()
    }

    fun stop() {
        isRunning = false
        wearableClientManager.dataClient.removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        val gson = GsonBuilder().registerTypeAdapter(
            ActiveDurationCheckpoint::class.java,
            ActiveDurationCheckpointSerializer(),
        ).registerTypeAdapter(
            ActiveDurationCheckpoint::class.java,
            ActiveDurationCheckpointDeserializer(),
        ).create()

        dataEvents.forEach { dataEvent ->
            when (dataEvent.dataItem.uri.path) {
                WearableClientManager.EXERCISE_DATA_PATH -> {
                    exerciseServiceState.update {
                        gson.fromJson(
                            dataEvent.dataItem.data?.toString(StandardCharsets.UTF_8),
                            ExerciseServiceState::class.java
                        )
                    }
                }
            }
        }
    }

    private fun collectExerciseMetrics() {
        coroutineScope.launch {
            while (isRunning) {
                var lastDistance = 0.0
                val exerciseState = exerciseServiceState.value.exerciseState
                when (exerciseState) {
                    ExerciseState.ACTIVE -> {
                        val exerciseMetrics = exerciseServiceState.value.exerciseMetrics
                        exerciseSessionData.update {
                            exerciseMetrics.toExerciseSessionData(lastDistance)
                        }
                        exerciseMetrics.distance?.let { lastDistance = it }
                    }

                    ExerciseState.ENDED -> break
                }
                delay(1_000)
            }
        }
    }

    private fun ExerciseMetrics.toExerciseSessionData(lastDistance: Double): ExerciseSessionData {
        return ExerciseSessionData(
            distance = distance?.let { it - lastDistance } ?: 0.0,
            heartRate = heartRate?.toLong(),
            pace = pace,
            cadence = cadence,
            location = location
        )
    }
}

class ActiveDurationCheckpointSerializer : JsonSerializer<ActiveDurationCheckpoint?> {
    @Throws(JsonParseException::class)
    override fun serialize(
        src: ActiveDurationCheckpoint?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonObject().apply {
            add("time", JsonPrimitive(src?.time.toString()))
            add("activeDuration", JsonPrimitive(src?.activeDuration.toString()))
        }
    }
}

class ActiveDurationCheckpointDeserializer : JsonDeserializer<ActiveDurationCheckpoint?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ActiveDurationCheckpoint {
        val map = json.asJsonObject.asMap()
        return ActiveDurationCheckpoint(
            Instant.parse(map["time"]?.asString),
            Duration.parse(map["activeDuration"]?.asString)
        )
    }
}
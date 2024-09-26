package com.ssafy.presentation.core.exercise

import androidx.health.services.client.data.ExerciseState
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ExerciseMonitor_PACEMAKER"

@Singleton
class ExerciseMonitor @Inject constructor(
    private val wearableClientManager: WearableClientManager,
    private val coroutineScope: CoroutineScope,
) : DataClient.OnDataChangedListener {
    val exerciseServiceState = MutableStateFlow(ExerciseServiceState())

    val exerciseSessionData = MutableStateFlow<List<ExerciseSessionData>>(listOf())

    private var isConnect = false

    fun connect() {
        isConnect = true
        exerciseSessionData.update { listOf() }
        wearableClientManager.dataClient.addListener(this)
        coroutineScope.launch {
            collectExerciseMetrics()
        }
    }

    fun disconnect() {
        isConnect = false
        wearableClientManager.dataClient.removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { dataEvent ->
            when (dataEvent.dataItem.uri.path) {
                WearableClientManager.EXERCISE_DATA_PATH -> {
                    exerciseServiceState.update {
                        Gson().fromJson(
                            dataEvent.dataItem.data?.toString(StandardCharsets.UTF_8),
                            ExerciseServiceState::class.java
                        )
                    }
                }
            }
        }
    }

    private suspend fun collectExerciseMetrics() {
        while (isConnect) {
            var lastDistance = 0.0
            val exerciseState = exerciseServiceState.value.exerciseState
            when (exerciseState) {
                ExerciseState.ACTIVE -> {
                    val exerciseMetrics = exerciseServiceState.value.exerciseMetrics
                    exerciseSessionData.update {
                        exerciseSessionData.value.toMutableList().apply {
                            add(exerciseMetrics.toExerciseSessionData(lastDistance))
                        }
                    }
                    exerciseMetrics.distance?.let { lastDistance = it }
                }

                ExerciseState.ENDED -> break
            }
            delay(1_000)
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
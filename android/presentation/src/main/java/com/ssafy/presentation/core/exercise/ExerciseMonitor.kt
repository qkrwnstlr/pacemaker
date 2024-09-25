package com.ssafy.presentation.core.exercise

import android.util.Log
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

private const val TAG = "ExerciseMonitor_PACEMAKER"

class ExerciseMonitor @Inject constructor(
    private val wearableClientManager: WearableClientManager,
    private val coroutineScope: CoroutineScope,
) : DataClient.OnDataChangedListener {
    val exerciseServiceState = MutableStateFlow(ExerciseServiceState())

    val exerciseSessionData = mutableListOf<ExerciseSessionData>()

    fun connect() {
        exerciseSessionData.clear()
        wearableClientManager.dataClient.addListener(this)
        coroutineScope.launch {
            collectExerciseSessionData()
        }
    }

    fun disconnect() {
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

    private suspend fun collectExerciseSessionData() {
        while (true) {
            val exerciseState = exerciseServiceState.value.exerciseState
            when (exerciseState) {
                ExerciseState.ACTIVE -> {
                    val exerciseMetrics = exerciseServiceState.value.exerciseMetrics
                    Log.d(TAG, "collectExerciseSessionData: ${exerciseMetrics.toExerciseSessionData()}")
                    exerciseSessionData.add(exerciseMetrics.toExerciseSessionData())
                }

                ExerciseState.ENDED -> break
            }
            delay(1_000)
        }
    }

    private fun ExerciseMetrics.toExerciseSessionData(): ExerciseSessionData {
        return ExerciseSessionData(
            heartRate = heartRate?.toLong(),
            pace = pace,
            cadence = cadence,
            location = location
        )
    }
}
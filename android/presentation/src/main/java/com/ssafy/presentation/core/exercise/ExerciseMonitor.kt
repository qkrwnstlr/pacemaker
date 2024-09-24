package com.ssafy.presentation.core.exercise

import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class ExerciseMonitor @Inject constructor(
    private val wearableClientManager: WearableClientManager,
) : DataClient.OnDataChangedListener {
    val exerciseServiceState = MutableStateFlow(ExerciseServiceState())

    fun connect() {
        wearableClientManager.dataClient.addListener(this)
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
}
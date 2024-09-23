package com.ssafy.pacemaker.service

import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.gson.Gson
import com.ssafy.pacemaker.data.ExerciseClientManager
import com.ssafy.pacemaker.data.WearableClientManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import javax.inject.Inject

private const val TAG = "ExerciseMonitor_PACEMAKER"

class ExerciseMonitor @Inject constructor(
    private val wearableClientManager: WearableClientManager,
    private val exerciseClientManager: ExerciseClientManager,
    private val coroutineScope: CoroutineScope
) : DataClient.OnDataChangedListener {
    fun connect() {
        wearableClientManager.dataClient.addListener(this)
    }

    fun disconnect() {
        wearableClientManager.dataClient.removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { dataEvent ->
            val frozenEvent = dataEvent.freeze()
            coroutineScope.launch {
                Log.d(TAG, "onDataChanged: ${frozenEvent.dataItem.uri.path}")
                when (frozenEvent.dataItem.uri.path) {
                    WearableClientManager.START_RUN_PATH -> exerciseClientManager.startExercise()
                    WearableClientManager.PAUSE_RUN_PATH -> exerciseClientManager.pauseExercise()
                    WearableClientManager.END_RUN_PATH -> exerciseClientManager.endExercise()
                    WearableClientManager.RESUME_RUN_PATH -> exerciseClientManager.resumeExercise()
                }
            }
        }
    }
}
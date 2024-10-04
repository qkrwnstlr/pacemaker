package com.ssafy.pacemaker.service

import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.ssafy.pacemaker.data.ExerciseClientManager
import com.ssafy.pacemaker.data.WearableClientManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseMonitor @Inject constructor(
    private val wearableClientManager: WearableClientManager,
    private val exerciseClientManager: ExerciseClientManager,
    private val coroutineScope: CoroutineScope
) : MessageClient.OnMessageReceivedListener {
    fun connect() {
        wearableClientManager.messageClient.addListener(this)
    }

    fun disconnect() {
        wearableClientManager.messageClient.removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        coroutineScope.launch {
            when (messageEvent.path) {
                WearableClientManager.START_RUN_PATH -> exerciseClientManager.startExercise()
                WearableClientManager.PAUSE_RUN_PATH -> exerciseClientManager.pauseExercise()
                WearableClientManager.END_RUN_PATH -> exerciseClientManager.endExercise()
                WearableClientManager.RESUME_RUN_PATH -> exerciseClientManager.resumeExercise()
            }
        }
    }
}
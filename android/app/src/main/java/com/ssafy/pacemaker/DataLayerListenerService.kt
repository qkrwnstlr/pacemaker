package com.ssafy.pacemaker

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.ssafy.presentation.core.MainActivity
import com.ssafy.presentation.core.exercise.ExerciseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class DataLayerListenerService : WearableListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when (messageEvent.path) {
            START_ACTIVITY_PATH -> {
                Intent(applicationContext, MainActivity::class.java).apply {
                    action = MainActivity.RUNNING_ACTION
                }.run {
                    this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(this)
                }
                startForegroundService(Intent(this, ExerciseService::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        private const val START_ACTIVITY_PATH = "/start-activity"
    }
}

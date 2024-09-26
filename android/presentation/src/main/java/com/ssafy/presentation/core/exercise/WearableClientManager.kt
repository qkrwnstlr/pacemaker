package com.ssafy.presentation.core.exercise

import android.content.Context
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearableClientManager @Inject constructor(
    @ApplicationContext val context: Context
) {
    val dataClient by lazy { Wearable.getDataClient(context) }
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    suspend fun sendToWearableDevice(path: String) {
        val nodes = capabilityClient
            .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
            .await()
            .nodes

        nodes.map { node ->
            messageClient.sendMessage(node.id, path, byteArrayOf()).await()
        }
    }

    suspend fun startWearableActivity() {
        val nodes = capabilityClient
            .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
            .await()
            .nodes

        nodes.map { node ->
            messageClient.sendMessage(node.id, START_ACTIVITY_PATH, byteArrayOf()).await()
        }
    }

    companion object {
        const val START_ACTIVITY_PATH = "/start-activity"
        const val START_RUN_PATH = "/start-run"
        const val PAUSE_RUN_PATH = "/pause-run"
        const val END_RUN_PATH = "/end-run"
        const val RESUME_RUN_PATH = "/resume-run"
        const val EXERCISE_DATA_PATH = "/exercise-data"

        private const val WEAR_CAPABILITY = "wear"
    }
}
package com.ssafy.presentation.core

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

private const val TAG = "WearableClientManager_PACEMAKER"

@Singleton
class WearableClientManager @Inject constructor(
    @ApplicationContext val context: Context
) {
    val dataClient by lazy { Wearable.getDataClient(context) }
    val messageClient by lazy { Wearable.getMessageClient(context) }
    val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    suspend fun <T> sendToHandheldDevice(path: String, data: T): DataItem {
        val request = PutDataMapRequest.create("$path")
            .apply { dataMap.putString("data", Gson().toJson(data)) }
            .asPutDataRequest()
            .setUrgent()

        return dataClient.putDataItem(request).await()
    }

    suspend fun startWearableActivity() {
        val nodes = capabilityClient
            .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
            .await()
            .nodes

        Log.d(TAG, "startWearableActivity: ${nodes.size}")

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
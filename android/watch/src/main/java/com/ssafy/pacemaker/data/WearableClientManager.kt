package com.ssafy.pacemaker.data

import android.content.Context
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class WearableClientManager @Inject constructor(
    @ApplicationContext val context: Context
) {
    val dataClient by lazy { Wearable.getDataClient(context) }
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    suspend fun <T> sendToMobileDevice(path: String, data: T): DataItem {
        val request = PutDataRequest
            .create("$path")
            .apply { setData(Gson().toJson(data).toByteArray()) }

        return dataClient.putDataItem(request).await()
    }

    suspend fun startMobileActivity() {
        val nodes = capabilityClient
            .getCapability(MOBILE_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
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

        private const val MOBILE_CAPABILITY = "mobile"
    }
}
package com.ssafy.pacemaker.data

import android.content.Context
import android.util.Log
import androidx.health.services.client.data.ExerciseUpdate.ActiveDurationCheckpoint
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
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
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Singleton
import java.lang.reflect.Type
import java.time.Duration


private const val TAG = "WearableClientManager_PACEMAKER"

@Singleton
class WearableClientManager @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val dataClient by lazy { Wearable.getDataClient(context) }
    val messageClient by lazy { Wearable.getMessageClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    suspend fun <T> sendToMobileDevice(path: String, data: T): DataItem {
        val gson = GsonBuilder().registerTypeAdapter(
            ActiveDurationCheckpoint::class.java,
            ActiveDurationCheckpointSerializer(),
        ).registerTypeAdapter(
            ActiveDurationCheckpoint::class.java,
            ActiveDurationCheckpointDeserializer(),
        ).create()

        val request = PutDataRequest.create(path)
            .apply { setData(gson.toJson(data).toByteArray()) }

        Log.d(TAG, "sendToMobileDevice: ${data}")
        Log.d(TAG, "sendToMobileDevice: ${gson.toJson(data)}")

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
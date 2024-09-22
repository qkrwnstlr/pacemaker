package com.ssafy.presentation.runningUI

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.ssafy.presentation.core.WearableClientManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val TAG = "RunningViewModel_PACEMAKER"

@HiltViewModel
class RunningViewModel @Inject constructor(
    private val wearableClientManager: WearableClientManager
) : ViewModel(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    fun initWearableClient() {
        wearableClientManager.dataClient.addListener(this)
        wearableClientManager.messageClient.addListener(this)
        wearableClientManager.capabilityClient.addListener(
            this,
            Uri.parse("wear://"),
            CapabilityClient.FILTER_REACHABLE
        )

        viewModelScope.launch {
            wearableClientManager.startWearableActivity()
            wearableClientManager.sendToHandheldDevice(WearableClientManager.START_RUN_PATH, Unit)
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { dataEvent ->
            Log.d(TAG, "onDataChanged: ${dataEvent.dataItem.uri.path}")
            Log.d(TAG, "onDataChanged: ${dataEvent.dataItem.data?.toString(Charsets.UTF_8)}")
            when (dataEvent.dataItem.uri.path) {
                WearableClientManager.PAUSE_RUN_PATH -> pauseExercise()
                WearableClientManager.RESUME_RUN_PATH -> resumeExercise()
                WearableClientManager.END_RUN_PATH -> endExercise()
                WearableClientManager.EXERCISE_DATA_PATH -> {}
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "onMessageReceived: ${messageEvent.data}")
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(TAG, "onCapabilityChanged: ${capabilityInfo}")
    }

    fun pauseExercise() {
        viewModelScope.launch {
            wearableClientManager.sendToHandheldDevice(WearableClientManager.PAUSE_RUN_PATH, Unit)
        }
    }

    fun endExercise() {
        viewModelScope.launch {
            wearableClientManager.sendToHandheldDevice(WearableClientManager.END_RUN_PATH, Unit)
        }
    }

    fun resumeExercise() {
        viewModelScope.launch {
            wearableClientManager.sendToHandheldDevice(WearableClientManager.RESUME_RUN_PATH, Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        wearableClientManager.dataClient.removeListener(this)
        wearableClientManager.messageClient.removeListener(this)
        wearableClientManager.capabilityClient.removeListener(this)
    }
}
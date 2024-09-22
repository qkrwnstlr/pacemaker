package com.ssafy.pacemaker.presentation.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.pacemaker.data.HealthServicesRepository
import com.ssafy.pacemaker.data.ServiceState
import com.ssafy.pacemaker.data.WearableClientManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository,
    private val wearableClientManager: WearableClientManager
) : ViewModel() {
    init {
        viewModelScope.launch {
            healthServicesRepository.prepareExercise()
        }
    }

    val uiState: StateFlow<ExerciseScreenState> = healthServicesRepository.serviceState.map {
        ExerciseScreenState(
            hasExerciseCapabilities = healthServicesRepository.hasExerciseCapability(),
            isTrackingAnotherExercise = healthServicesRepository.isTrackingExerciseInAnotherApp(),
            serviceState = it,
            exerciseState = (it as? ServiceState.Connected)?.exerciseServiceState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3_000),
        healthServicesRepository.serviceState.value.let {
            ExerciseScreenState(
                hasExerciseCapabilities = true,
                isTrackingAnotherExercise = false,
                serviceState = it,
                exerciseState = (it as? ServiceState.Connected)?.exerciseServiceState
            )
        }

    )

    fun startExercise() {
        viewModelScope.launch {
            wearableClientManager.startWearableActivity()
            wearableClientManager.sendToHandheldDevice(WearableClientManager.START_RUN_PATH, Unit)
        }
        healthServicesRepository.startExercise()
    }

    fun pauseExercise() {
        viewModelScope.launch {
            wearableClientManager.sendToHandheldDevice(WearableClientManager.PAUSE_RUN_PATH, Unit)
        }
        healthServicesRepository.pauseExercise()
    }

    fun endExercise() {
        viewModelScope.launch {
            wearableClientManager.sendToHandheldDevice(WearableClientManager.END_RUN_PATH, Unit)
        }
        healthServicesRepository.endExercise()
    }

    fun resumeExercise() {
        viewModelScope.launch {
            wearableClientManager.sendToHandheldDevice(WearableClientManager.RESUME_RUN_PATH, Unit)
        }
        healthServicesRepository.resumeExercise()
    }
}




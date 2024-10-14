package com.ssafy.pacemaker.presentation.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.pacemaker.data.HealthServicesRepository
import com.ssafy.pacemaker.data.ServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository,
) : ViewModel() {
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

    suspend fun isExerciseInProgress() = healthServicesRepository.isExerciseInProgress()

    fun pauseExercise() {
        healthServicesRepository.pauseExercise()
    }

    fun endExercise() {
        healthServicesRepository.endExercise()
    }

    fun resumeExercise() {
        healthServicesRepository.resumeExercise()
    }
}

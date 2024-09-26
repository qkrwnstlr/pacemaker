package com.ssafy.pacemaker.presentation.home

import androidx.health.services.client.data.ExerciseState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.pacemaker.data.HealthServicesRepository
import com.ssafy.pacemaker.data.ServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository,
): ViewModel() {
    init {
        healthServicesRepository.prepareExercise()
    }

    fun startExercise() {
        healthServicesRepository.startExercise()
    }


    fun collectServiceState(navigateToExerciseRoute: () -> Unit) {
        viewModelScope.launch {
            healthServicesRepository.serviceState.collect {
                when (it) {
                    is ServiceState.Disconnected -> {}
                    is ServiceState.Connected -> {
                        when (it.exerciseServiceState.exerciseState) {
                            ExerciseState.ACTIVE -> navigateToExerciseRoute()
                        }
                    }
                }
            }
        }
    }
}
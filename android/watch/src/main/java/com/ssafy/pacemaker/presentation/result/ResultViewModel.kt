package com.ssafy.pacemaker.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.pacemaker.data.HealthServicesRepository
import com.ssafy.pacemaker.data.ServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository,
) : ViewModel() {
    fun onFinish() {
        healthServicesRepository.clearExercise()
    }

    fun collectServiceState(navigateToHomeRoute: () -> Unit) {
        viewModelScope.launch {
            healthServicesRepository.serviceState.collect {
                when (it) {
                    is ServiceState.Disconnected -> {
                        navigateToHomeRoute()
                    }

                    is ServiceState.Connected -> {}
                }
            }
        }
    }
}
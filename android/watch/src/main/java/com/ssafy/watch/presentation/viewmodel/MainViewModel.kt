package com.ssafy.watch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.watch.data.HealthServicesBackgroundRepository
import com.ssafy.watch.data.PassiveDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthDataViewModel @Inject constructor(
    private val healthServicesBackgroundRepository: HealthServicesBackgroundRepository,
    private val passiveDataRepository: PassiveDataRepository
) : ViewModel() {
    val hrValue = passiveDataRepository.latestHeartRate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Double.NaN)

    init {
        viewModelScope.launch {
            val supported = healthServicesBackgroundRepository.hasHeartRateCapability()

            passiveDataRepository.setPassiveDataEnabled(supported)
            if (!supported) {
                passiveDataRepository.storeLatestHeartRate(Double.NaN)
            }
        }

        viewModelScope.launch {
            passiveDataRepository.passiveDataEnabled.distinctUntilChanged().collect { enabled ->
                if (enabled) {
                    healthServicesBackgroundRepository.registerForHealthService()
                } else {
                    healthServicesBackgroundRepository.unregisterForHealthService()
                }
            }
        }
    }
}

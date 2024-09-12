package com.ssafy.watch.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.watch.presentation.utils.HealthServicesManager
import com.ssafy.watch.presentation.utils.MeasureMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val healthServicesManager: HealthServicesManager) :
    ViewModel() {

    private val _hr = MutableStateFlow(0.0)
    val hr: StateFlow<Double> = _hr

    val enabled: MutableState<Boolean> = mutableStateOf(false)
    val availability: MutableState<DataTypeAvailability?> = mutableStateOf(null)

    init {
        viewModelScope.launch {
            enabled.value = healthServicesManager.hasHeartRateCapability()
            healthServicesManager
                .heartRateMeasureFlow()
                .takeWhile { enabled.value }
                .collect { measureMessage ->
                    when (measureMessage) {
                        is MeasureMessage.MeasureData -> {
                            val latestHeartRateValue = measureMessage.data.last().value
                            _hr.value = latestHeartRateValue
                        }

                        is MeasureMessage.MeasureAvailability -> availability.value =
                            measureMessage.availability
                    }
                }
        }
    }
}
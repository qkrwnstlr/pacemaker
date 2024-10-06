package com.ssafy.pacemaker.presentation.result

import androidx.lifecycle.ViewModel
import com.ssafy.pacemaker.data.HealthServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository,
) : ViewModel() {
    fun onFinish() {
        healthServicesRepository.clearExercise()
    }
}
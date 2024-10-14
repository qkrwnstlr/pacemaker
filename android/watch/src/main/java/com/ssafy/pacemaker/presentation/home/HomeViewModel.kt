package com.ssafy.pacemaker.presentation.home

import androidx.lifecycle.ViewModel
import com.ssafy.pacemaker.data.HealthServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository,
) : ViewModel() {
    fun startExercise() {
        healthServicesRepository.startExercise()
    }
}
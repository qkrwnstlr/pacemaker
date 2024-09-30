package com.ssafy.presentation.runningUI

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.presentation.core.exercise.ExerciseRepository
import com.ssafy.presentation.core.exercise.ServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "RunningViewModel_PACEMAKER"

@HiltViewModel
class RunningViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
) : ViewModel() {

    val uiState: StateFlow<RunningScreenState> = exerciseRepository.serviceState.map {
        RunningScreenState(
            serviceState = it,
            exerciseState = (it as? ServiceState.Connected)?.exerciseServiceState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3_000),
        exerciseRepository.serviceState.value.let {
            RunningScreenState(
                serviceState = it,
                exerciseState = (it as? ServiceState.Connected)?.exerciseServiceState
            )
        }
    )

    fun pauseExercise() {
        exerciseRepository.pauseExercise()
    }

    fun endExercise() {
        exerciseRepository.endExercise()
    }

    fun resumeExercise() {
        exerciseRepository.resumeExercise()
    }
}
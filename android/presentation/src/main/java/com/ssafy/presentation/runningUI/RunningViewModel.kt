package com.ssafy.presentation.runningUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.gson.Gson
import com.ssafy.presentation.core.ExerciseRepository
import com.ssafy.presentation.core.ExerciseServiceState
import com.ssafy.presentation.core.ServiceState
import com.ssafy.presentation.core.WearableClientManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class RunningViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    init {
        exerciseRepository.startExercise()
    }

    val exerciseState: StateFlow<RunningScreenState> = exerciseRepository.serviceState.map {
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
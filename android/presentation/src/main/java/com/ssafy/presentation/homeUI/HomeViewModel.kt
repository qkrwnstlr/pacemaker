package com.ssafy.presentation.homeUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.presentation.core.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _trainingState: MutableStateFlow<Int> = MutableStateFlow(4)
    val trainingState: StateFlow<Int> = _trainingState.asStateFlow()

    private val _coachState: MutableStateFlow<Int> = MutableStateFlow(1)
    val coachState: StateFlow<Int> = _coachState.asStateFlow()

    fun startExercise() {
        viewModelScope.launch {
            delay(3_000)
            exerciseRepository.startExercise()
        }
    }
}
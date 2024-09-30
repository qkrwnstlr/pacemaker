package com.ssafy.presentation.homeUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.presentation.core.exercise.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val exerciseRepository: ExerciseRepository,
    private val getPlanInfoUseCase: GetPlanInfoUseCase
) : ViewModel() {
    private val _trainingState: MutableStateFlow<Int> = MutableStateFlow(4)
    val trainingState: StateFlow<Int> = _trainingState.asStateFlow()

    private val _coachState: MutableStateFlow<Int> = MutableStateFlow(1)
    val coachState: StateFlow<Int> = _coachState.asStateFlow()

    private val _selectDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val selectDate: StateFlow<LocalDate> = _selectDate.asStateFlow()

    fun profileUrlFlow(): Flow<String> {
        return dataStoreRepository.getImgUrl()
    }

    fun startExercise() {
        viewModelScope.launch {
            exerciseRepository.startExercise()
        }
    }

    fun getPlanInfo() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { getPlanInfoUseCase() }
            .onSuccess { _trainingState.emit(3) }
            .onFailure { _trainingState.emit(4) }
    }

    fun setDate(localDate: LocalDate) = _selectDate.update { localDate }

}
package com.ssafy.presentation.homeUI

import androidx.lifecycle.ViewModel
import com.ssafy.domain.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _trainingState: MutableStateFlow<Int> = MutableStateFlow(4)
    val trainingState: StateFlow<Int> = _trainingState.asStateFlow()

    private val _coachState: MutableStateFlow<Int> = MutableStateFlow(1)
    val coachState: StateFlow<Int> = _coachState.asStateFlow()

    fun profileUrlFlow(): Flow<String> {
        return dataStoreRepository.getImgUrl()
    }
}
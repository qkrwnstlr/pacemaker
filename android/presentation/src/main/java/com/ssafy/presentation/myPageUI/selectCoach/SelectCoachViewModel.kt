package com.ssafy.presentation.myPageUI.selectCoach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.usecase.user.SetCoachUseCase
import com.ssafy.presentation.R
import com.ssafy.presentation.utils.ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCoachViewModel @Inject constructor(
    private val setCoachUseCase: SetCoachUseCase
) : ViewModel() {

    private val _selectCoachState: MutableStateFlow<Pair<Long, Int>> = MutableStateFlow(0L to 0)
    val selectCoachState: StateFlow<Pair<Long, Int>> = _selectCoachState.asStateFlow()

    private val _voiceEvent: MutableSharedFlow<Int> = MutableSharedFlow()
    val voiceEvent: SharedFlow<Int> = _voiceEvent.asSharedFlow()

    fun setCoach(
        coachIndex: Long,
        moveToNextFragment: suspend () -> Unit,
        failToSetCoach: suspend (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { setCoachUseCase(coachIndex) }
            .onSuccess { moveToNextFragment() }
            .onFailure { failToSetCoach(it.message ?: ERROR) }
            .also { _selectCoachState.emit(0L to 0) }
    }

    fun selectCoach(coachIndex: Long) = viewModelScope.launch(Dispatchers.IO) {
        val prevState = selectCoachState.value
        val index = prevState.first
        val count = prevState.second

        if (coachIndex == index) _selectCoachState.update { prevState.copy(second = count + 1) }
        else _selectCoachState.update { coachIndex to 1 }

        if (selectCoachState.value.second == INTRO) emitCoachPath(coachIndex)
    }

    private suspend fun emitCoachPath(coachIndex: Long) {
        val rawRes = when (coachIndex) {
            MIKE -> R.raw.mike
            JAMIE -> R.raw.jamie
            DANNY -> R.raw.danny
            else -> 0
        }

        if (rawRes != 0) _voiceEvent.emit(rawRes)
    }

    companion object {
        const val MIKE = 1L
        const val JAMIE = 2L
        const val DANNY = 3L

        const val INTRO = 1
    }
}

package com.ssafy.presentation.myPageUI.selectCoach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.usecase.train.GetTTSUseCase
import com.ssafy.domain.usecase.user.SetCoachUseCase
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
    private val setCoachUseCase: SetCoachUseCase,
    private val getTTSUseCase: GetTTSUseCase
) : ViewModel() {

    private val _selectCoachState: MutableStateFlow<Pair<Long, Int>> = MutableStateFlow(0L to 0)
    val selectCoachState: StateFlow<Pair<Long, Int>> = _selectCoachState.asStateFlow()

    private val _voiceEvent: MutableSharedFlow<String> = MutableSharedFlow()
    val voiceEvent: SharedFlow<String> = _voiceEvent.asSharedFlow()

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

        val message = getMessage()
        if (message.first.isBlank() || message.second == 0L) return@launch

        runCatching { getTTSUseCase(message = message.first, coachIndex = message.second) }
            .onSuccess { voicePath -> _voiceEvent.emit(voicePath) }
            .onFailure { it.printStackTrace() }
    }

    private fun getMessage(): Pair<String, Long> {
        val (index, count) = selectCoachState.value
        val isIntro = count == INTRO

        return when (index) {
            MIKE -> (if (isIntro) MIKE_INTRO else "") to MIKE
            JAMIE -> (if (isIntro) JAMIE_INTRO else "") to JAMIE
            DANNY -> (if (isIntro) DANNY_INTRO else "") to DANNY
            else -> "" to 0
        }
    }

    companion object {
        const val MIKE = 1L
        const val JAMIE = 2L
        const val DANNY = 3L

        const val INTRO = 1
        const val MIKE_INTRO = "안녕하세요, 열정 넘치는 러닝 코치 마이크예요! 함께 달리며 한계를 넘어볼까요?"
        const val JAMIE_INTRO = "안녕하세요! 저는 여러분의 든든한 러닝 파트너가 되고 싶은 제이미예요. 함께 즐겁고 건강한 러닝 습관을 만들어볼까요?"
        const val DANNY_INTRO = "어이구! 반갑습니다, 반가워요! 제가 바로 뛰는 재미 알려드릴 도리스 러닝 코치입니다! 함께 뛰어볼랍니까?"
    }
}

package com.ssafy.presentation.myPageUI.selectCoach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.response.ResponseResult
import com.ssafy.domain.usecase.user.SetCoachUseCase
import com.ssafy.presentation.utils.ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCoachViewModel @Inject constructor(
    private val setCoachUseCase: SetCoachUseCase
) : ViewModel() {

    fun setCoach(
        uid: String,
        coachIndex: Long,
        popBack: suspend () -> Unit,
        failToSetCoach: suspend (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { setCoachUseCase(uid, coachIndex) }
            .onSuccess { if (it is ResponseResult.Success) popBack() else failToSetCoach(it.message) }
            .onFailure { failToSetCoach(ERROR) }
    }

    companion object {
        const val MIKE = 1L
        const val JAMIE = 2L
        const val DANNY = 3L
    }
}

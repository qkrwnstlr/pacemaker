package com.ssafy.presentation.planUI.startPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.usecase.user.GetCoachUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartPlanViewModel @Inject constructor(
    private val getCoachUseCase: GetCoachUseCase
) : ViewModel() {

    fun checkCoach(
        uid: String,
        moveToRegisterPlanFragment: suspend () -> Unit,
        moveToSelectCoachFragment: suspend () -> Unit,
        failToGetCoach: suspend () -> Unit,
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { getCoachUseCase(uid) }
            .onSuccess {
                it.data?.coachNumber?.let { moveToRegisterPlanFragment() }
                    ?: moveToSelectCoachFragment()
            }.onFailure {
                failToGetCoach()
            }
    }

}

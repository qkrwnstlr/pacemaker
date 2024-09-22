package com.ssafy.presentation.planUI.registerPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.usecase.user.GetCoachUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterPlanViewModel @Inject constructor(
    private val getCoachUseCase: GetCoachUseCase
) : ViewModel() {

    private var coachIndex: Long? = null

    fun getCoach(uid: String) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { getCoachUseCase(uid) }
            .onSuccess { it.data?.coachNumber?.let { number -> coachIndex = number } }
    }

}
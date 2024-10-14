package com.ssafy.presentation.planUI.startPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartPlanViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    fun checkCoach(
        moveToRegisterPlanFragment: suspend () -> Unit,
        moveToSelectCoachFragment: suspend () -> Unit,
        failToGetCoach: suspend () -> Unit,
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { dataStoreRepository.getUser() }
            .onSuccess {
                if (it.coachNumber == 0L) moveToSelectCoachFragment()
                else moveToRegisterPlanFragment()
            }.onFailure {
                failToGetCoach()
            }
    }

}

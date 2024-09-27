package com.ssafy.presentation.scheduleUI.planDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.usecase.plan.DeletePlanUseCase
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.presentation.utils.ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    private val getPlanInfoUseCase: GetPlanInfoUseCase,
    private val deletePlanUseCase: DeletePlanUseCase
) : ViewModel() {

    private val _planInfo: MutableStateFlow<PlanInfo?> = MutableStateFlow(null)
    val planInfo: StateFlow<PlanInfo?> = _planInfo.asStateFlow()

    fun getPlanInfo(failToGetPlanInfo: suspend (String) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { getPlanInfoUseCase() }
                .onSuccess { _planInfo.emit(it) }
                .onFailure {
                    it.printStackTrace()
                    failToGetPlanInfo(it.message ?: ERROR)
                }
        }

    fun deletePlan(
        uid: String,
        successToDeletePlan: suspend () -> Unit,
        failToDeletePlan: suspend (String) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        val result = runCatching { deletePlanUseCase(uid) }
        withContext(Dispatchers.Main) {
            result
                .onSuccess { successToDeletePlan() }
                .onFailure { failToDeletePlan(it.message ?: ERROR) }
        }
    }

}

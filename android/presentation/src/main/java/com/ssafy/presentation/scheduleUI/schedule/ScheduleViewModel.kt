package com.ssafy.presentation.scheduleUI.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.usecase.plan.GetPlanDotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getPlanDotUseCase: GetPlanDotUseCase
) : ViewModel() {
    private val _dotList = MutableStateFlow<List<LocalDate>>(emptyList())
    val dotList = _dotList.asStateFlow()

    fun setMonthHasTrain(uid: String, year: Int, month: Int) {
        val resultList: MutableList<LocalDate> = arrayListOf()
        viewModelScope.launch {
            runCatching { getPlanDotUseCase.invoke(uid, year, month) }
                .onSuccess { response ->
                    response.data?.let {
                        resultList.clear()
                        resultList.addAll(it.trainDateList)
                        resultList.addAll(it.dailyTrainDateList)
                        _dotList.emit(resultList)
                    }
                }
        }
    }
}
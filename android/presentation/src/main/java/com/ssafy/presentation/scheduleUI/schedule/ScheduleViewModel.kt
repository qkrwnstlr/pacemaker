package com.ssafy.presentation.scheduleUI.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.schedule.DayContentData
import com.ssafy.domain.dto.schedule.ProgressData
import com.ssafy.domain.usecase.plan.GetPlanDotUseCase
import com.ssafy.domain.usecase.plan.GetProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getPlanDotUseCase: GetPlanDotUseCase,
    private val getProgressUseCase: GetProgressUseCase
) : ViewModel() {
    private val _dotList = MutableStateFlow<List<LocalDate>>(emptyList())
    val dotList = _dotList.asStateFlow()

    private val _dateList = MutableStateFlow<List<DayContentData>>(emptyList())
    val dateList = _dateList.asStateFlow()

    fun setMonthHasTrain(uid: String, year: Int, month: Int) {
        val resultList: MutableList<LocalDate> = arrayListOf()
        viewModelScope.launch {

            runCatching { getPlanDotUseCase.invoke(uid, year, month) }
                .onSuccess { response ->
                    response.data?.let { _dateList.emit(it) }
                    resultList.clear()
                    response.data?.map { LocalDate.parse(it.date) }?.let { resultList.addAll(it) }
                    _dotList.emit(resultList)
                }
        }
    }

    fun dateProgressInfo(date: LocalDate, setProgressView: (ProgressData) -> Unit) =
        viewModelScope.launch {
            runCatching { getProgressUseCase.invoke(date) }
                .onSuccess { result ->
                    result.data?.let { setProgressView(it) }
                }
        }
}
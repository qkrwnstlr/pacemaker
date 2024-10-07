package com.ssafy.presentation.scheduleUI.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.dto.schedule.ProgressData
import com.ssafy.domain.usecase.plan.GetProgressUseCase
import com.ssafy.domain.usecase.reports.GetReportUseCase
import com.ssafy.domain.usecase.user.GetCalendarDotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getCalendarDotUseCase: GetCalendarDotUseCase,
    private val getProgressUseCase: GetProgressUseCase,
    private val getReportUseCase: GetReportUseCase
) : ViewModel() {
    private val _dateList = MutableStateFlow<Map<String, List<ContentListDto>>>(emptyMap())
    val dateList = _dateList.asStateFlow()

    fun setMonthHasTrain(uid: String, year: Int, month: Int) {
        viewModelScope.launch {
            runCatching { getCalendarDotUseCase.invoke(uid, year, month) }
                .onSuccess { response ->
                    val currentData = _dateList.value.toMutableMap()
                    currentData.putAll(response)
                    _dateList.emit(currentData)
                }
        }
    }

    fun dateProgressInfo(uid: String, date: LocalDate, setProgressView: (ProgressData?) -> Unit) =
        viewModelScope.launch {
            runCatching { getProgressUseCase(uid, date) }
                .onSuccess { data -> setProgressView(data) }
                .onFailure { setProgressView(null) }
        }

    fun getReport(item: ContentListDto, callback: (Report) -> Unit) {
        viewModelScope.launch {
            runCatching { getReportUseCase.invoke(item) }
                .onSuccess { callback(it) }
                .onFailure { it.printStackTrace() }

        }
    }

}
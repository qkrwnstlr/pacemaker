package com.ssafy.presentation.homeUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.domain.usecase.reports.GetReportUseCase
import com.ssafy.domain.usecase.user.GetTermCalendarDotUseCase
import com.ssafy.presentation.core.exercise.ExerciseRepository
import com.ssafy.presentation.utils.toCoachIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val exerciseRepository: ExerciseRepository,
    private val getPlanInfoUseCase: GetPlanInfoUseCase,
    private val getTermCalendarDotUseCase: GetTermCalendarDotUseCase,
    private val getReportUseCase: GetReportUseCase
) : ViewModel() {
    private val _trainingState: MutableStateFlow<Int> = MutableStateFlow(3)
    val trainingState: StateFlow<Int> = _trainingState.asStateFlow()

    private val _coachState: MutableStateFlow<Int> = MutableStateFlow(1)
    val coachState: StateFlow<Int> = _coachState.asStateFlow()

    private val _selectDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val selectDate: StateFlow<LocalDate> = _selectDate.asStateFlow()

    private val _dateList = MutableStateFlow<Map<String, List<ContentListDto>>>(emptyMap())
    val dateList = _dateList.asStateFlow()

    private val _report = MutableStateFlow(Report())
    val report = _report.asStateFlow()

    fun getCoach() = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            dataStoreRepository.getUser().coachNumber.toCoachIndex()
        }
            .onSuccess { _coachState.emit(it) }
    }

    fun setTermMonthHasTrain(
        uid: String,
        startYear: Int,
        startMonth: Int,
        currentYear: Int,
        currentMonth: Int,
        endYear: Int,
        endMonth: Int
    ) {
        viewModelScope.launch {
            runCatching {
                getTermCalendarDotUseCase.invoke(
                    uid, startYear, startMonth, currentYear, currentMonth, endYear, endMonth
                )
            }.onSuccess {
                _dateList.emit(it)
            }
        }
    }

    fun profileUrlFlow(): Flow<String> {
        return dataStoreRepository.getImgUrl()
    }

    fun startExercise() {
        viewModelScope.launch {
            exerciseRepository.startExercise()
        }
    }

    fun getPlanInfo() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { getPlanInfoUseCase() }.onSuccess {
            _report.emit(Report(PlanTrain()))
            _trainingState.emit(3)
        }
            .onFailure {
                _report.emit(Report(null))
                _trainingState.emit(4)
            }
    }

    fun setDate(localDate: LocalDate) = viewModelScope.launch(Dispatchers.IO) {
        _selectDate.update { localDate }
        if (dateList.value.containsKey(localDate.toString())) {
            val reportList = dateList.value[localDate.toString()]
            if (reportList != null) {
                for (i in reportList.indices) {
                    if (reportList[i].type == "BEFORE_PLAN_TRAIN") {
                        _report.emit(
                            getReportUseCase.invoke(
                                reportList[i],
                                dataStoreRepository.getUser().uid
                            )
                        )
                        _trainingState.emit(1)
                    } else if (i == reportList.size - 1) {
                        _report.emit(
                            getReportUseCase.invoke(
                                reportList[i],
                                dataStoreRepository.getUser().uid
                            )
                        )
                        _trainingState.emit(2)
                    }
                }
            }
        } else {
            getPlanInfo()
        }
    }
}
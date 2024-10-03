package com.ssafy.presentation.homeUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.domain.usecase.reports.GetReportUseCase
import com.ssafy.domain.usecase.user.GetCalendarInfoUseCase
import com.ssafy.presentation.core.exercise.ExerciseRepository
import com.ssafy.presentation.utils.toCoachIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val exerciseRepository: ExerciseRepository,
    private val getPlanInfoUseCase: GetPlanInfoUseCase,
    private val getCalendarInfoUseCase: GetCalendarInfoUseCase,
    private val getReportUseCase: GetReportUseCase
) : ViewModel() {

    private val _coachState: MutableStateFlow<Int> = MutableStateFlow(1)
    val coachState: StateFlow<Int> = _coachState.asStateFlow()

    private val _selectDate: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val selectDate: StateFlow<LocalDate> = _selectDate.asStateFlow()

    private val _dateList = MutableStateFlow<Map<String, List<ContentListDto>>>(emptyMap())
    val dateList = _dateList.asStateFlow()

    private val _report = MutableStateFlow(Report())
    val report = _report.asStateFlow()

    suspend fun setLocation(latitude: Double, longitude: Double) {
        dataStoreRepository.setLocation(latitude, longitude)
    }

    suspend fun getLatitude(): Double = dataStoreRepository.getLatitude().firstOrNull() ?: 0.0

    suspend fun getLongitude(): Double = dataStoreRepository.getLongitude().firstOrNull() ?: 0.0

    fun getCoach() = viewModelScope.launch(Dispatchers.IO) {
        runCatching { dataStoreRepository.getUser().coachNumber.toCoachIndex() }
            .onSuccess { _coachState.emit(it) }
    }

    fun getTrain() = viewModelScope.launch(Dispatchers.IO) {
        val date = LocalDate.now()
        runCatching {
            getCalendarInfoUseCase(date)
        }.onSuccess {
            _dateList.emit(it)
            setDateTrainInfo(date)
        }.onFailure {
            it.printStackTrace()
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

    fun setDate(localDate: LocalDate) = viewModelScope.launch(Dispatchers.IO) {
        _selectDate.update { localDate }
        setDateTrainInfo(localDate)
    }

    private suspend fun setDateTrainInfo(localDate: LocalDate) {
        val reportList = dateList.value[localDate.toString()] ?: emptyList()

        if (reportList.isEmpty()) getPlanInfo(localDate)
        else getReportInfo(reportList, localDate)
    }

    private suspend fun getPlanInfo(localDate: LocalDate) = runCatching {
        getPlanInfoUseCase()
    }.onSuccess {
        val newReport = Report(
            planTrain = PlanTrain(),
            trainingState = TRAIN_REST_MESSAGE,
            date = localDate
        )
        _report.emit(newReport)
    }.onFailure {
        val newReport = Report(
            trainingState = CREATE_PLAN,
            date = localDate
        )
        _report.emit(newReport)
    }

    private suspend fun getReportInfo(reportList: List<ContentListDto>, localDate: LocalDate) =
        runCatching {
            val reportGroup = reportList.groupBy { it.type }
            val content = when {
                !reportGroup[PLAN_REPORT].isNullOrEmpty() -> reportGroup[PLAN_REPORT]?.lastOrNull()
                !reportGroup[BEFORE_PLAN_TRAIN].isNullOrEmpty() -> reportGroup[BEFORE_PLAN_TRAIN]?.lastOrNull()
                !reportGroup[FREE_REPORT].isNullOrEmpty() -> reportGroup[FREE_REPORT]?.lastOrNull()
                else -> null
            }

            content?.let { getReportUseCase(it) }
        }.onSuccess { reportInfo ->
            if (reportInfo == null) return@onSuccess

            val newReport = if (reportInfo.trainReport == null) {
                reportInfo.copy(trainingState = TRAIN_INFO, date = localDate)
            } else {
                reportInfo.copy(trainingState = TRAIN_RESULT, date = localDate)
            }

            _report.emit(newReport)
        }.onFailure {
            it.printStackTrace()
        }

    companion object {
        const val FREE_REPORT = "FREE_REPORT"
        const val PLAN_REPORT = "PLAN_REPORT"
        const val BEFORE_PLAN_TRAIN = "BEFORE_PLAN_TRAIN"

        const val TRAIN_INFO = 1
        const val TRAIN_RESULT = 2
        const val TRAIN_REST_MESSAGE = 3
        const val CREATE_PLAN = 4
    }
}
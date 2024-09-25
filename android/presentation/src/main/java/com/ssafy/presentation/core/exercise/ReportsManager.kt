package com.ssafy.presentation.core.exercise

import androidx.health.connect.client.records.HeartRateRecord
import com.google.gson.Gson
import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.SplitData
import com.ssafy.domain.dto.reports.TrainResult
import com.ssafy.domain.usecase.reports.CreatePlanReportsUseCase
import javax.inject.Inject

class ReportsManager @Inject constructor(
    private val planManager: PlanManager,
    private val createPlanReportsUseCase: CreatePlanReportsUseCase
) {
    suspend fun createPlanReports(
        exerciseMetrics: ExerciseMetrics,
        exerciseSessionData: List<ExerciseSessionData>
    ) {
        val trainResult = TrainResult(
            exerciseMetrics.distance?.toInt() ?: 0,
            exerciseSessionData.time.seconds.toInt(),
            exerciseMetrics.heartRateAverage?.toInt() ?: 0,
            (exerciseMetrics.paceAverage?.toInt() ?: 0) / 1000,
            exerciseMetrics.cadence?.toInt() ?: 0,
            exerciseMetrics.calories?.toInt() ?: 0,
            exerciseSessionData.heartRate.zone,
            exerciseSessionData.splitData,
            Gson().toJson(exerciseSessionData.location),
            listOf(), // TODO : 코치 메시지 바꾸기
        )

        val request = CreatePlanReportsRequest(
            planManager.user.uid,
            0L, // TODO : planManager.plan.id
            planManager.user.coachNumber,
            exerciseSessionData.last().time.toString(),
            trainResult,
        )
        createPlanReportsUseCase(request)
    }

    private val List<HeartRateRecord.Sample>.zone: List<Int>
        get() = this.groupBy {
            when (it.beatsPerMinute) {
                in 98..117 -> 1
                in 118..137 -> 2
                in 138..156 -> 3
                in 157..176 -> 4
                in 177..196 -> 5
                else -> "기타"
            }
        }.map { it.value.size }

    private val List<ExerciseSessionData>.splitData: List<SplitData>
        get() = this.map {
            SplitData(
                it.distance?.toInt() ?: 0,
                it.pace?.toInt() ?: 0,
                it.heartRate?.toInt() ?: 0
            )
        }
}
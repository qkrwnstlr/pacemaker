package com.ssafy.presentation.core.exercise.manager

import androidx.health.connect.client.records.HeartRateRecord
import com.ssafy.domain.dto.reports.SplitData
import com.ssafy.domain.dto.reports.TrainResult
import com.ssafy.domain.usecase.reports.CreatePlanReportsUseCase
import com.ssafy.presentation.core.exercise.data.ExerciseData
import com.ssafy.presentation.core.exercise.data.ExerciseSessionData
import com.ssafy.presentation.core.exercise.data.cadenceAvg
import com.ssafy.presentation.core.exercise.data.distance
import com.ssafy.presentation.core.exercise.data.duration
import com.ssafy.presentation.core.exercise.data.heartRate
import com.ssafy.presentation.core.exercise.data.heartRateAvg
import com.ssafy.presentation.core.exercise.data.location
import com.ssafy.presentation.core.exercise.data.paceAvg
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportsManager @Inject constructor(
    private val createPlanReportsUseCase: CreatePlanReportsUseCase
) {
    suspend fun createPlanReports(
        planTrainId: Long,
        exerciseData: ExerciseData,
    ) {
        val trainResult = TrainResult(
            exerciseData.totalDistance.inMeters.toInt(),
            exerciseData.duration.seconds.toInt(),
            exerciseData.heartRateAvg.toInt(),
            exerciseData.paceAvg.toInt(),
            exerciseData.cadenceAvg.toInt(),
            exerciseData.totalEnergyBurned.inKilocalories.toInt(),
            exerciseData.heartRate.zone,
            exerciseData.sessions.map { it.splitData },
            exerciseData.location.map { listOf(it.latitude, it.longitude) },
        )

        runCatching {
            createPlanReportsUseCase(planTrainId, trainResult)
        }.onFailure {

        }
    }

    private val List<HeartRateRecord.Sample>.zone: List<Int>
        get() = this.filter { it.beatsPerMinute in 98..196 }.groupBy {
            when (it.beatsPerMinute) {
                in 98..117 -> 1
                in 118..137 -> 2
                in 138..156 -> 3
                in 157..176 -> 4
                in 177..196 -> 5
                else -> ""
            }
        }.map { it.value.size }

    private val List<ExerciseSessionData>.splitData: SplitData
        get() = SplitData(
            this.distance.toInt(),
            this.paceAvg.toInt(),
            this.heartRateAvg.toInt(),
            this.cadenceAvg.toInt()
        )
}
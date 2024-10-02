package com.ssafy.presentation.core.exercise.manager

import androidx.health.connect.client.records.HeartRateRecord
import com.ssafy.domain.dto.reports.SplitData
import com.ssafy.domain.dto.reports.TrainResult
import com.ssafy.domain.usecase.reports.CreateFreeReportsUseCase
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
    private val createPlanReportsUseCase: CreatePlanReportsUseCase,
    private val createFreeReportsUseCase: CreateFreeReportsUseCase
) {
    suspend fun createReports(
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
            if (planTrainId != 0L) createPlanReportsUseCase(planTrainId, trainResult)
            else createFreeReportsUseCase(trainResult)
        }.onFailure {

        }
    }

    private val List<HeartRateRecord.Sample>.zone: List<Int>
        get() = listOf(
            this.count { it.beatsPerMinute in 98..117 },
            this.count { it.beatsPerMinute in 118..137 },
            this.count { it.beatsPerMinute in 138..156 },
            this.count { it.beatsPerMinute in 157..176 },
            this.count { it.beatsPerMinute in 177..196 },
        )

    private val List<ExerciseSessionData>.splitData: SplitData
        get() = SplitData(
            this.distance.toInt(),
            this.paceAvg.toInt(),
            this.heartRateAvg.toInt(),
            this.cadenceAvg.toInt()
        )
}
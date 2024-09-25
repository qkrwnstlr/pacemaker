package com.ssafy.pacemaker.service

import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseGoal
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate.ActiveDurationCheckpoint
import androidx.health.services.client.data.LocationAvailability
import androidx.health.services.client.data.LocationData

data class ExerciseMetrics(
    val heartRate: Double? = null,
    val distance: Double? = null,
    val calories: Double? = null,
    val heartRateAverage: Double? = null,
    val pace: Double? = null,
    val paceAverage: Double? = null,
    val steps: Long? = null,
    val cadence: Long? = null,
    val cadenceAverage: Long? = null,
    val vo2: Double? = null,
    val vo2Average: Double? = null,
    val location: LocationData? = null,
) {
    fun update(latestMetrics: DataPointContainer): ExerciseMetrics {
        return copy(
            heartRate = latestMetrics.getData(DataType.HEART_RATE_BPM).lastOrNull()?.value
                ?: heartRate,
            distance = latestMetrics.getData(DataType.DISTANCE_TOTAL)?.total ?: distance,
            calories = latestMetrics.getData(DataType.CALORIES_TOTAL)?.total ?: calories,
            heartRateAverage = latestMetrics.getData(DataType.HEART_RATE_BPM_STATS)?.average
                ?: heartRateAverage,
            pace = latestMetrics.getData(DataType.PACE).lastOrNull()?.value ?: pace,
            paceAverage = latestMetrics.getData(DataType.PACE_STATS)?.average ?: paceAverage,
            steps = latestMetrics.getData(DataType.RUNNING_STEPS_TOTAL)?.total ?: steps,
            cadence = latestMetrics.getData(DataType.STEPS_PER_MINUTE).lastOrNull()?.value
                ?: cadence,
            cadenceAverage = latestMetrics.getData(DataType.STEPS_PER_MINUTE_STATS)?.average
                ?: cadenceAverage,
            vo2 = latestMetrics.getData(DataType.VO2_MAX).lastOrNull()?.value ?: vo2,
            vo2Average = latestMetrics.getData(DataType.VO2_MAX_STATS)?.average ?: vo2Average,
            location = latestMetrics.getData(DataType.LOCATION).lastOrNull()?.value ?: location,
        )
    }
}

data class ExerciseServiceState(
    val exerciseState: ExerciseState? = null,
    val exerciseMetrics: ExerciseMetrics = ExerciseMetrics(),
    val exerciseLaps: Int = 0,
    val activeDurationCheckpoint: ActiveDurationCheckpoint? = null,
    val locationAvailability: LocationAvailability = LocationAvailability.UNKNOWN,
    val error: String? = null,
    val exerciseGoal: Set<ExerciseGoal<out Number>> = emptySet()
)
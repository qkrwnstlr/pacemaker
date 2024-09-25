package com.ssafy.presentation.core.exercise

import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.calories
import androidx.health.connect.client.units.kilometersPerHour
import androidx.health.connect.client.units.meters
import androidx.health.services.client.data.LocationData
import java.time.Duration
import java.time.ZonedDateTime

data class ExerciseData(
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val totalSteps: Long? = null,
    val totalDistance: Length? = null,
    val totalEnergyBurned: Energy? = null,
)

data class ExerciseSessionData(
    val time: ZonedDateTime = ZonedDateTime.now(),
    val distance: Double? = null,
    val heartRate: Long? = null,
    val pace: Double? = null,
    val cadence: Long? = null,
    val location: LocationData? = null,
)

val List<ExerciseSessionData>.location: List<ExerciseRoute.Location>
    get() = this.filter {
        it.location != null
    }.map {
        ExerciseRoute.Location(
            it.time.toInstant(),
            it.location!!.latitude,
            it.location.longitude,
        )
    }

val List<ExerciseSessionData>.distance: Double
    get() = this.filter {
        it.distance != null
    }.sumOf {
        it.distance!!
    }

val List<ExerciseSessionData>.heartRate: List<HeartRateRecord.Sample>
    get() = this.filter {
        it.heartRate != null
    }.map {
        HeartRateRecord.Sample(
            time = it.time.toInstant(),
            beatsPerMinute = it.heartRate!!
        )
    }

val List<ExerciseSessionData>.cadence: List<StepsCadenceRecord.Sample>
    get() = this.filter {
        it.cadence != null
    }.map {
        StepsCadenceRecord.Sample(
            time = it.time.toInstant(),
            rate = it.cadence!!.toDouble()
        )
    }

val List<ExerciseSessionData>.speed: List<SpeedRecord.Sample>
    get() = this.filter {
        it.pace != null
    }.map {
        SpeedRecord.Sample(
            time = it.time.toInstant(),
            speed = (it.pace!! / 1000 * 60 * 60).kilometersPerHour
        )
    }

val List<ExerciseSessionData>.time: Duration
    get() = Duration.between(this.first().time, this.last().time)

fun parseExerciseData(
    exerciseMetrics: ExerciseMetrics,
    exerciseSessionData: List<ExerciseSessionData>
): ExerciseData {
    return ExerciseData(
        exerciseSessionData.first().time,
        exerciseSessionData.last().time.plusSeconds(1),
        exerciseMetrics.steps,
        exerciseMetrics.distance?.meters,
        exerciseMetrics.calories?.calories,
    )
}
package com.ssafy.presentation.core.exercise.data

import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.units.metersPerSecond
import java.time.Duration
import java.time.ZonedDateTime

val List<ExerciseSessionData>.location: List<ExerciseRoute.Location>
    get() = this.filter {
        it.location != null
    }.map {
        ExerciseRoute.Location(
            it.time.toInstant(),
            it.location!!.latitude,
            it.location.longitude,
        )
    }.filter { it.time < this.endTime.toInstant() }

val List<ExerciseSessionData>.distance: Double
    get() = this.filter {
        it.distance != null
    }.sumOf {
        it.distance!!
    }

val List<ExerciseSessionData>.heartRate: List<HeartRateRecord.Sample>
    get() = this.filter {
        it.heartRate != null && it.heartRate != 0L
    }.map {
        HeartRateRecord.Sample(
            time = it.time.toInstant(),
            beatsPerMinute = it.heartRate!!
        )
    }.filter { it.time < this.endTime.toInstant() }

val List<ExerciseSessionData>.heartRateAvg: Double
    get() = this.heartRate.map { it.beatsPerMinute }.average()

val List<ExerciseSessionData>.cadence: List<StepsCadenceRecord.Sample>
    get() = this.filter {
        it.cadence != null
    }.map {
        StepsCadenceRecord.Sample(
            time = it.time.toInstant(),
            rate = it.cadence!!.toDouble()
        )
    }.filter { it.time < this.endTime.toInstant() }

val List<ExerciseSessionData>.cadenceAvg: Double
    get() = this.cadence.map { it.rate }.average()

val List<ExerciseSessionData>.speed: List<SpeedRecord.Sample>
    get() = this.filter {
        it.speed != null
    }.map {
        SpeedRecord.Sample(
            time = it.time.toInstant(),
            speed = it.speed!!.metersPerSecond
        )
    }.filter { it.time < this.endTime.toInstant() }

val List<ExerciseSessionData>.speedAvg: Double
    get() = this.speed.map { it.speed.inKilometersPerHour }.average()

val List<ExerciseSessionData>.paceAvg: Double
    get() = 60 * 60 / this.speedAvg

val List<ExerciseSessionData>.startTime: ZonedDateTime
    get() = this.firstOrNull()?.time ?: ZonedDateTime.now()

val List<ExerciseSessionData>.endTime: ZonedDateTime
    get() = this.lastOrNull()?.time ?: ZonedDateTime.now()

val List<ExerciseSessionData>.duration: Duration
    get() = Duration.between(this.startTime, this.endTime)

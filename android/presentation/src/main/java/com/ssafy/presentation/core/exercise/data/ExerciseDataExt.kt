package com.ssafy.presentation.core.exercise.data

import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import java.time.Duration
import java.time.ZonedDateTime

val ExerciseData.location: List<ExerciseRoute.Location>
    get() = this.sessions.map { it.location }.flatten()

val ExerciseData.distance: Double
    get() = this.sessions.sumOf { it.distance }

val ExerciseData.heartRate: List<HeartRateRecord.Sample>
    get() = this.sessions.map { it.heartRate }.flatten()

val ExerciseData.heartRateAvg: Double
    get() = this.sessions.map { it.heartRateAvg }.average()

val ExerciseData.cadence: List<StepsCadenceRecord.Sample>
    get() = this.sessions.map { it.cadence }.flatten()

val ExerciseData.cadenceAvg: Double
    get() = this.sessions.map { it.cadenceAvg }.average()

val ExerciseData.speed: List<SpeedRecord.Sample>
    get() = this.sessions.map { it.speed }.flatten()

val ExerciseData.speedAvg: Double
    get() = this.sessions.map { it.speedAvg }.average()

val ExerciseData.paceAvg: Double
    get() = 60 * 60 / this.speedAvg

val ExerciseData.startTime: ZonedDateTime
    get() = this.sessions.firstOrNull()?.firstOrNull()?.time ?: ZonedDateTime.now()

val ExerciseData.endTime: ZonedDateTime
    get() = this.sessions.lastOrNull()?.lastOrNull()?.time ?: ZonedDateTime.now()

val ExerciseData.duration: Duration
    get() = Duration.between(this.startTime, this.endTime)

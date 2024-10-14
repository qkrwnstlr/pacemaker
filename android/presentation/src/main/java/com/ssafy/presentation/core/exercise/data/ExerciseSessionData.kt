package com.ssafy.presentation.core.exercise.data

import androidx.health.services.client.data.LocationData
import java.time.ZonedDateTime

data class ExerciseSessionData(
    val time: ZonedDateTime = ZonedDateTime.now(),
    val distance: Double? = null,
    val heartRate: Long? = null,
    val speed: Double? = null,
    val pace: Double? = null,
    val cadence: Long? = null,
    val location: LocationData? = null,
)
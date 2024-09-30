package com.ssafy.presentation.core.exercise.data

import androidx.health.services.client.data.LocationData

data class ExerciseMetrics(
    val heartRate: Double? = null,
    val pace: Double? = null,
    val steps: Long? = null,
    val cadence: Long? = null,
    val vo2: Double? = null,
    val distance: Double? = null,
    val calories: Double? = null,
    val location: LocationData? = null,
)
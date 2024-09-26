package com.ssafy.presentation.core.exercise

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
)
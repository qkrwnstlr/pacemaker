package com.ssafy.presentation.core.healthConnect

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import java.time.Duration

data class ExerciseSessionDetail(
    val uid: String,
    val totalActiveTime: Duration? = null,
    val totalSteps: Long? = null,
    val totalDistance: Length? = null,
    val totalEnergyBurned: Energy? = null,
    val avgHeartRate: Long? = null,
    val avgCadence: Double? = null,
)
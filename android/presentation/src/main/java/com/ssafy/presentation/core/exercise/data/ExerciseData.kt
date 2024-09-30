package com.ssafy.presentation.core.exercise.data

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length

data class ExerciseData(
    val totalSteps: Long = 1L,
    val totalDistance: Length = Length.meters(0.0),
    val totalEnergyBurned: Energy = Energy.calories(0.0),
    val sessions: List<List<ExerciseSessionData>> = listOf()
)
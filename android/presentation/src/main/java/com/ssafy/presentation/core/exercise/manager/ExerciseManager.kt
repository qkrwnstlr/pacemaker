package com.ssafy.presentation.core.exercise.manager

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.ssafy.presentation.core.exercise.ExerciseMonitor
import com.ssafy.presentation.core.exercise.data.ExerciseData
import com.ssafy.presentation.core.exercise.data.ExerciseMetrics
import com.ssafy.presentation.core.exercise.data.ExerciseSessionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseManager @Inject constructor(
    private val exerciseMonitor: ExerciseMonitor,
    private val coroutineScope: CoroutineScope,
) {
    private var isConnected = false
    private var isDuringSession = false

    val exerciseServiceState = exerciseMonitor.exerciseServiceState

    val exerciseData = MutableStateFlow(ExerciseData())

    val currentSessionData = MutableStateFlow<List<ExerciseSessionData>>(listOf())

    fun connect() {
        exerciseMonitor.run()
        exerciseData.update { ExerciseData() }
        currentSessionData.update { listOf() }
    }

    fun disconnect() {
        isConnected = false
        exerciseMonitor.stop()
    }

    fun startSession() {
        isDuringSession = true
        currentSessionData.update { listOf() }
        coroutineScope.launch { collectExerciseSessionData() }
    }

    fun stopSession() {
        isDuringSession = false
        exerciseData.update {
            it.copy(sessions = it.sessions.toMutableList().apply { add(currentSessionData.value) })
        }
    }

    private suspend fun collectExerciseSessionData() {
        exerciseMonitor.exerciseSessionData.collect { exerciseSessionData ->
            if (isDuringSession) {
                updateExerciseData(exerciseServiceState.value.exerciseMetrics)
                currentSessionData.update {
                    currentSessionData.value.toMutableList().apply { add(exerciseSessionData) }
                }
            }
        }
    }

    private fun updateExerciseData(exerciseMetrics: ExerciseMetrics) {
        exerciseData.update {
            it.copy(
                totalSteps = exerciseMetrics.steps ?: it.totalSteps,
                totalDistance = exerciseMetrics.distance?.let(Length::meters) ?: it.totalDistance,
                totalEnergyBurned = exerciseMetrics.calories?.let(Energy::calories) ?: it.totalEnergyBurned,
            )
        }
    }
}
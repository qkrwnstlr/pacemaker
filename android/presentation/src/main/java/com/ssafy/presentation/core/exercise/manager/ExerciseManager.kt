package com.ssafy.presentation.core.exercise.manager

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.ssafy.presentation.core.exercise.ExerciseMonitor
import com.ssafy.presentation.core.exercise.data.ExerciseData
import com.ssafy.presentation.core.exercise.data.ExerciseMetrics
import com.ssafy.presentation.core.exercise.data.ExerciseSessionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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

    private var updateCurrentSessionJob: Job? = null
    private var updateExerciseDataJob: Job? = null

    val exerciseServiceState = exerciseMonitor.exerciseServiceState

    val exerciseData = MutableStateFlow(ExerciseData())

    val currentSessionData = MutableStateFlow<List<ExerciseSessionData>>(listOf())

    fun connect() {
        if (isConnected) return
        isConnected = true
        exerciseMonitor.run()
        exerciseData.update { ExerciseData() }
        currentSessionData.update { listOf() }
    }

    fun disconnect() {
        isConnected = false
        exerciseMonitor.stop()
    }

    fun startRunning() {
        currentSessionData.update { listOf() }
        collectExerciseSessionData()
        collectExerciseServiceState()
    }

    fun stopRunning() {
        exerciseData.update {
            it.copy(sessions = it.sessions.toMutableList().apply { add(currentSessionData.value) })
        }
        updateCurrentSessionJob?.cancel()
        updateExerciseDataJob?.cancel()
        updateCurrentSessionJob = null
        updateExerciseDataJob = null
    }

    fun startJogging() {
        currentSessionData.update { listOf() }
        collectExerciseSessionData()
        collectExerciseServiceState()
    }

    fun stopJogging() {
        updateCurrentSessionJob?.cancel()
        updateExerciseDataJob?.cancel()
        updateCurrentSessionJob = null
        updateExerciseDataJob = null
    }

    private fun collectExerciseSessionData() {
        if (updateCurrentSessionJob != null) return
        updateCurrentSessionJob = coroutineScope.launch {
            exerciseMonitor.exerciseSessionData.collect { exerciseSessionData ->
                currentSessionData.update {
                    currentSessionData.value.toMutableList().apply { add(exerciseSessionData) }
                }
            }
        }
    }

    private fun collectExerciseServiceState() {
        if (updateExerciseDataJob != null) return
        updateExerciseDataJob = coroutineScope.launch {
            exerciseMonitor.exerciseServiceState.collect {
                updateExerciseData(it.exerciseMetrics)
            }
        }
    }

    private fun updateExerciseData(exerciseMetrics: ExerciseMetrics) {
        exerciseData.update {
            it.copy(
                totalSteps = exerciseMetrics.steps ?: it.totalSteps,
                totalDistance = exerciseMetrics.distance?.let(Length::meters) ?: it.totalDistance,
                totalEnergyBurned = exerciseMetrics.calories?.let(Energy::kilocalories)
                    ?: it.totalEnergyBurned,
            )
        }
    }
}
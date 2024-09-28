package com.ssafy.presentation.core.exercise.manager

import androidx.health.connect.client.units.Velocity
import androidx.health.services.client.data.ExerciseState
import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.usecase.train.GetCoachingUseCase
import com.ssafy.presentation.core.exercise.ExerciseMonitor
import com.ssafy.presentation.core.exercise.data.ExerciseSessionData
import com.ssafy.presentation.core.exercise.data.cadence
import com.ssafy.presentation.core.exercise.data.distance
import com.ssafy.presentation.core.exercise.data.heartRate
import com.ssafy.presentation.core.exercise.data.speed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoachingManager @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val exerciseMonitor: ExerciseMonitor,
    private val planManager: PlanManager,
    private val getCoachingUseCase: GetCoachingUseCase,
) {
    private var isConnected = false

    val coachVoicePath = MutableStateFlow("")

    fun connect() {
        isConnected = true
        coroutineScope.launch {
            runCatching {
                planManager.syncPlanInfo { disconnect() }
                collectExerciseSessionData()
            }.onFailure {
                disconnect()
            }
        }
    }

    fun disconnect() {
        isConnected = false
    }

    private suspend fun collectExerciseSessionData() {
        val collectedList = mutableListOf<ExerciseSessionData>()

        coroutineScope.launch {
            while (isConnected) {
                delay(2 * 1_000 * 60)

                val state = exerciseMonitor.exerciseServiceState.value
                val exerciseState = state.exerciseState
                when (exerciseState) {
                    ExerciseState.ACTIVE -> {
                        val distance = state.exerciseMetrics.distance?.toFloat() ?: 0f
                        getCoaching(distance, collectedList)
                        collectedList.clear()
                    }

                    ExerciseState.ENDED -> break
                }
            }
        }

        exerciseMonitor.exerciseSessionData.collect(collectedList::add)
    }

    private suspend fun getCoaching(totalDistance: Float, list: List<ExerciseSessionData>) {
        val coachingRequestDto = list.toCoachingRequest(totalDistance)
        runCatching {
            getCoachingUseCase(coachingRequestDto)
        }.onSuccess { filePath ->
            coachVoicePath.emit(filePath)
        }
    }

    private fun List<ExerciseSessionData>.toCoachingRequest(totalDistance: Float): CoachingRequest {
        return CoachingRequest(
            planManager.coach,
            totalDistance,
            distance.toFloat(),
            heartRate.map { it.beatsPerMinute }.average().toInt(),
            speed.map { it.speed.pace }.average().toInt(),
            cadence.map { it.rate }.average().toInt(),
            planManager.plan,
        )
    }

    private val Velocity.pace
        get() = 60 * 60 / inKilometersPerHour
}
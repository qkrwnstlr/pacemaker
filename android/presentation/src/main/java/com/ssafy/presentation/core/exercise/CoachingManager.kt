package com.ssafy.presentation.core.exercise

import androidx.health.connect.client.units.Velocity
import androidx.health.services.client.data.ExerciseState
import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.CoachingResponse
import com.ssafy.domain.usecase.train.GetCoachingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
    private var isConnect = false

    val coachingResponse = MutableStateFlow(CoachingResponse("", ""))

    fun connect() {
        isConnect = true
        coachingResponse.update { CoachingResponse("", "") }
        coroutineScope.launch {
            runCatching {
                planManager.syncPlanInfo { disconnect() }
                collectExerciseSessionData()
            }.onFailure {
                disconnect()
            }
            collectExerciseSessionData()
        }
    }

    fun disconnect() {
        isConnect = false
    }

    private suspend fun collectExerciseSessionData() {
        val list = mutableListOf<ExerciseSessionData>()

        coroutineScope.launch {
            while (isConnect) {
                delay(2 * 1_000 * 60)
                val exerciseState = exerciseMonitor.exerciseServiceState.value.exerciseState
                when (exerciseState) {
                    ExerciseState.ACTIVE -> {
                        getCoaching(
                            exerciseMonitor.exerciseServiceState.value.exerciseMetrics.distance?.toFloat()
                                ?: 0.0f, list
                        )
                        list.clear()
                    }

                    ExerciseState.ENDED -> break
                }
            }
        }

        exerciseMonitor.exerciseSessionData.collect {
            it.lastOrNull()?.let { list.add(it) }
        }
    }

    private suspend fun getCoaching(totalDistance: Float, list: List<ExerciseSessionData>) {
        val coachingRequestDto = list.toCoachingRequest(totalDistance)
        runCatching {
            getCoachingUseCase(coachingRequestDto).data
        }.onSuccess {
            coachingResponse.update { it }
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
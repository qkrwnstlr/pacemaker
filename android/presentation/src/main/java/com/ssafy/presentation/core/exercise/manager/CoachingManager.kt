package com.ssafy.presentation.core.exercise.manager

import androidx.health.connect.client.units.Velocity
import androidx.health.services.client.data.ExerciseState
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.usecase.train.GetCoachingUseCase
import com.ssafy.presentation.core.exercise.ExerciseMonitor
import com.ssafy.presentation.core.exercise.data.ExerciseSessionData
import com.ssafy.presentation.core.exercise.data.cadence
import com.ssafy.presentation.core.exercise.data.distance
import com.ssafy.presentation.core.exercise.data.heartRate
import com.ssafy.presentation.core.exercise.data.speed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
    private val getCoachingUseCase: GetCoachingUseCase,
) {
    private var isConnected = false

    private var collectSessionDataJob: Job? = null
    private var getCoachingJob: Job? = null

    private lateinit var train: PlanTrain

    val coachVoicePath = MutableStateFlow<String?>(null)

    fun connect(train: PlanTrain) {
        if (isConnected) return
        isConnected = true
        coachVoicePath.update { null }
        this.train = train
        collectExerciseSessionData()
    }

    fun disconnect() {
        isConnected = false
        getCoachingJob?.cancel()
        collectSessionDataJob?.cancel()
        getCoachingJob = null
        collectSessionDataJob = null
    }

    private fun collectExerciseSessionData() {
        val collectedList = mutableListOf<ExerciseSessionData>()

        getCoachingJob = coroutineScope.launch {
            while (isConnected) {
                delay((1.5 * 60 * 1_000).toLong())
                if (!isConnected) break

                val state = exerciseMonitor.exerciseServiceState.value
                val exerciseState = state.exerciseState
                when (exerciseState) {
                    ExerciseState.ACTIVE -> {
                        val distance = state.exerciseMetrics.distance?.toFloat() ?: 0f
                        getCoaching(distance, collectedList, train)
                    }

                    ExerciseState.ENDED -> break
                }
            }
        }

        collectSessionDataJob = coroutineScope.launch {
            exerciseMonitor.exerciseSessionData.collect(collectedList::add)
        }
    }

    private suspend fun getCoaching(
        totalDistance: Float,
        exerciseSessionData: List<ExerciseSessionData>,
        train: PlanTrain
    ) {
        val coachingRequestDto = exerciseSessionData.toCoachingRequest(totalDistance, train)
        runCatching {
            getCoachingUseCase(coachingRequestDto)
        }.onSuccess { filePath ->
            coachVoicePath.emit(filePath)
        }
    }

    private fun List<ExerciseSessionData>.toCoachingRequest(
        totalDistance: Float,
        train: PlanTrain
    ): CoachingRequest {
        return CoachingRequest(
            totalDistance,
            distance.toFloat(),
            this.chunked(10).map { it.heartRate.map { it.beatsPerMinute }.average().toInt() },
            this.chunked(10).map { it.speed.map { it.speed.pace }.average().toInt() },
            this.chunked(10).map { it.cadence.map { it.rate }.average().toInt() },
            train,
        )
    }

    private val Velocity.pace
        get() = 60 * 60 / inKilometersPerHour
}
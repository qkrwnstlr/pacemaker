package com.ssafy.presentation.core.exercise

import androidx.health.connect.client.units.Velocity
import androidx.health.services.client.data.ExerciseState
import androidx.lifecycle.lifecycleScope
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.CoachingResponse
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.domain.usecase.train.GetCoachingUseCase
import com.ssafy.domain.utils.DANNY
import com.ssafy.domain.utils.DANNY_FEAT
import com.ssafy.domain.utils.JAMIE
import com.ssafy.domain.utils.JAMIE_FEAT
import com.ssafy.domain.utils.MIKE
import com.ssafy.domain.utils.MIKE_FEAT
import com.ssafy.presentation.utils.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

class CoachingMonitor @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val exerciseMonitor: ExerciseMonitor,
    private val dataStoreRepository: DataStoreRepository,
    private val getPlanInfoUseCase: GetPlanInfoUseCase,
    private val getCoachingUseCase: GetCoachingUseCase,
) {
    private var isConnect = false

    val coachingResponse = MutableStateFlow(CoachingResponse("", ""))

    private lateinit var coach: String
    private lateinit var plan: PlanInfo

    fun connect() {
        isConnect = true
        coachingResponse.update { CoachingResponse("", "") }
        coroutineScope.launch {
            val user = dataStoreRepository.getUser()
            coach = when (user.coachNumber) {
                MIKE -> MIKE_FEAT
                JAMIE -> JAMIE_FEAT
                DANNY -> DANNY_FEAT
                else -> ""
            }
            runCatching {
                getPlanInfoUseCase(user.uid)
            }.onSuccess {
                plan = it
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
        val meanPace = this.speed.map { it.speed.pace }.average().toInt()
        val time = Duration.between(this.first().time, this.last().time).seconds

        return CoachingRequest(
            coach,
            totalDistance,
            (time / meanPace).toFloat(),
            this.heartRate.map { it.beatsPerMinute }.average().toInt(),
            meanPace,
            this.cadence.map { it.rate }.average().toInt(),
            plan.planTrains.firstOrNull {
                it.trainDate.toLocalDate().atStartOfDay() == LocalDate.now().atStartOfDay()
            } ?: PlanTrain(),
        )
    }

    private val Velocity.pace
        get() = 60 * 60 / this.inKilometersPerHour
}
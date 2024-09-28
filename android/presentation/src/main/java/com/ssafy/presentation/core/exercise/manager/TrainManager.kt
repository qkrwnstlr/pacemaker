package com.ssafy.presentation.core.exercise.manager

import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.presentation.core.exercise.data.ExerciseSessionData
import com.ssafy.presentation.core.exercise.data.distance
import com.ssafy.presentation.core.exercise.data.duration
import com.ssafy.presentation.utils.toLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainManager @Inject constructor(
    private val getPlanInfoUseCase: GetPlanInfoUseCase
) {
    lateinit var train: PlanTrain
    private lateinit var running: TrainSession
    private lateinit var jogging: TrainSession

    val trainState: MutableStateFlow<TrainState> = MutableStateFlow(TrainState.Before)

    suspend fun connect(exerciseSessionFlow: Flow<List<ExerciseSessionData>>) {
        train = getPlanInfoUseCase().planTrains.firstOrNull {
            it.trainDate.toLocalDate().atStartOfDay() == LocalDate.now().atStartOfDay()
        } ?: PlanTrain()

        trainState.update { TrainState.Before }

        when (train.paramType) {
            "time" -> {
                running = TrainSession.timeRunning(train.trainParam)
                jogging = TrainSession.timeJogging(train.interParam)
            }

            "distance" -> {
                running = TrainSession.distanceRunning(train.trainParam)
                jogging = TrainSession.distanceJogging(train.interParam)
            }
        }

        collectExerciseSessionData(exerciseSessionFlow)
    }

    private suspend fun collectExerciseSessionData(
        exerciseSessionFlow: Flow<List<ExerciseSessionData>>,
    ) {
        exerciseSessionFlow.collect {
            val isTrainAchieved = when (train.paramType) {
                "time" -> checkIsAchieved(it.duration)
                "distance" -> checkIsAchieved(it.distance)
                else -> return@collect
            }

            if (isTrainAchieved) setToNextTrainState()
        }
    }

    private fun setToNextTrainState() = with(trainState.value) {
        trainState.update {
            when (this) {
                is TrainState.Before -> TrainState.WarmUp()

                is TrainState.WarmUp -> {
                    TrainState.During(running, 1)
                }

                is TrainState.During -> {
                    when (session.type) {
                        TrainSession.Type.RUNNING -> {
                            if (step <= train.repetition) TrainState.During(jogging, step)
                            else TrainState.CoolDown()
                        }

                        TrainSession.Type.JOGGING -> {
                            TrainState.During(running, step + 1)
                        }
                    }
                }

                is TrainState.CoolDown -> {
                    TrainState.Ended
                }

                TrainState.Ended -> return@with
            }
        }
    }

    private fun checkIsAchieved(duration: Duration) = with(trainState.value) {
        when (this) {
            TrainState.Before -> true
            is TrainState.WarmUp -> duration.seconds >= session.goal
            is TrainState.During -> duration.seconds >= session.goal
            is TrainState.CoolDown -> duration.seconds >= session.goal
            TrainState.Ended -> false
        }
    }

    private fun checkIsAchieved(distance: Double) = with(trainState.value) {
        when (this) {
            TrainState.Before -> true
            is TrainState.WarmUp -> distance >= session.goal
            is TrainState.During -> distance >= session.goal
            is TrainState.CoolDown -> distance >= session.goal
            TrainState.Ended -> false
        }
    }
}

sealed class TrainState {
    data object Before : TrainState()
    class WarmUp(val session: TrainSession = TrainSession.WARM_UP) : TrainState()
    class During(val session: TrainSession, val step: Int) : TrainState()
    class CoolDown(val session: TrainSession = TrainSession.COOL_DOWN) : TrainState()
    data object Ended : TrainState()
}

sealed class TrainSession(val type: Type, val goal: Int) {
    class Time(type: Type, goal: Int) : TrainSession(type, goal)
    class Distance(type: Type, goal: Int) : TrainSession(type, goal)

    companion object {
        fun timeRunning(goal: Int) = Time(Type.RUNNING, goal)
        fun distanceRunning(goal: Int) = Time(Type.JOGGING, goal)
        fun timeJogging(goal: Int) = Distance(Type.RUNNING, goal)
        fun distanceJogging(goal: Int) = Distance(Type.JOGGING, goal)

        val WARM_UP = timeJogging(Duration.ofMinutes(5).seconds.toInt())
        val COOL_DOWN = timeJogging(Duration.ofMinutes(5).seconds.toInt())
    }

    enum class Type {
        RUNNING,
        JOGGING;
    }
}
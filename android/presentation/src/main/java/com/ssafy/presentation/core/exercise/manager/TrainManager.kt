package com.ssafy.presentation.core.exercise.manager

import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.usecase.plan.GetPlanInfoUseCase
import com.ssafy.presentation.core.exercise.data.ExerciseSessionData
import com.ssafy.presentation.core.exercise.data.distance
import com.ssafy.presentation.core.exercise.data.duration
import com.ssafy.presentation.utils.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainManager @Inject constructor(
    private val getPlanInfoUseCase: GetPlanInfoUseCase,
    private val coroutineScope: CoroutineScope,
    private var exerciseManager: ExerciseManager
) {
    private var isConnected = false

    lateinit var train: PlanTrain
    private lateinit var running: TrainSession
    private lateinit var jogging: TrainSession

    val trainState: MutableStateFlow<TrainState> = MutableStateFlow(TrainState.Before)

    private var isUpdating = false

    suspend fun connect() {
        if (isConnected) return

        isUpdating = false
        trainState.emit(TrainState.Before)

        isConnected = true
        runCatching {
            getPlanInfoUseCase()
        }.onSuccess {
            train = it.planTrains.firstOrNull {
                it.status == "BEFORE" &&
                        it.trainDate.toLocalDate().atStartOfDay() == LocalDate.now().atStartOfDay()
            } ?: PlanTrain()
        }.onFailure {
            train = PlanTrain()
        }

        when (train.paramType) {
            "time" -> {
                running = TrainSession.timeRunning(train.trainParam, train.trainPace)
                jogging = TrainSession.timeJogging(train.interParam)
            }

            "distance" -> {
                running = TrainSession.distanceRunning(train.trainParam, train.trainPace)
                jogging = TrainSession.distanceJogging(train.interParam)
            }
        }

        collectExerciseSessionData()
    }

    fun disconnect() {
        isConnected = false
        trainState.update { TrainState.None }
    }

    private fun collectExerciseSessionData() {
        coroutineScope.launch {
            exerciseManager.currentSessionData.takeWhile { isConnected }.collect {
                if (!isUpdating) {
                    isUpdating = true
                    val isTrainAchieved = when (train.paramType) {
                        "time" -> checkIsAchieved(it.duration)
                        "distance" -> checkIsAchieved(it.distance)
                        else -> {
                            exerciseManager.startRunning()
                            trainState.update { TrainState.Default }
                            return@collect
                        }
                    }

                    if (isTrainAchieved) setToNextTrainState()
                    isUpdating = false
                }
            }
        }
    }

    private fun setToNextTrainState() = with(trainState.value) {
        trainState.update {
            when (this) {
                is TrainState.Before -> {
                    exerciseManager.startJogging()
                    TrainState.WarmUp()
                }

                is TrainState.WarmUp -> {
                    exerciseManager.stopJogging()
                    exerciseManager.startRunning()
                    TrainState.During(running, 1)
                }

                is TrainState.During -> {
                    when (session) {
                        is TrainSession.Running -> {
                            exerciseManager.stopRunning()
                            exerciseManager.startJogging()
                            if (step < train.repetition) TrainState.During(jogging, step)
                            else TrainState.CoolDown()
                        }

                        is TrainSession.Jogging -> {
                            exerciseManager.stopJogging()
                            exerciseManager.startRunning()
                            TrainState.During(running, step + 1)
                        }
                    }
                }

                is TrainState.CoolDown -> {
                    exerciseManager.stopJogging()
                    TrainState.Ended
                }

                else -> return@with
            }
        }
    }

    private fun checkIsAchieved(duration: Duration) = with(trainState.value) {
        when (this) {
            TrainState.None -> false
            TrainState.Before -> true
            is TrainState.WarmUp -> duration.seconds >= session.goal
            is TrainState.During -> duration.seconds >= session.goal
            is TrainState.CoolDown -> duration.seconds >= session.goal
            TrainState.Ended -> false
            TrainState.Default -> false
        }
    }

    private fun checkIsAchieved(distance: Double) = with(trainState.value) {
        when (this) {
            TrainState.None -> false
            TrainState.Before -> true
            is TrainState.WarmUp -> distance >= session.goal
            is TrainState.During -> distance >= session.goal
            is TrainState.CoolDown -> distance >= session.goal
            TrainState.Ended -> false
            TrainState.Default -> false
        }
    }

    fun skipWarmUp() {
        if (trainState.value is TrainState.WarmUp) {
            exerciseManager.stopJogging()
            exerciseManager.startRunning()
            trainState.update { TrainState.During(running, 1) }
        }
    }

    fun skipCoolDown() {
        if (trainState.value is TrainState.WarmUp) {
            exerciseManager.stopJogging()
            trainState.update { TrainState.Ended }
        }
    }

    fun finishTrain() {
        trainState.update { TrainState.Ended }
    }
}

sealed class TrainState {
    data object None : TrainState()
    data object Before : TrainState()
    class WarmUp(val session: TrainSession = TrainSession.WARM_UP) : TrainState()
    class During(val session: TrainSession, val step: Int) : TrainState()
    class CoolDown(val session: TrainSession = TrainSession.COOL_DOWN) : TrainState()
    data object Ended : TrainState()
    data object Default : TrainState()
}

sealed class TrainSession(val type: Type, val goal: Int) {
    class Running(type: Type, goal: Int, val pace: Int) : TrainSession(type, goal)
    class Jogging(type: Type, goal: Int) : TrainSession(type, goal)

    companion object {
        fun timeRunning(goal: Int, pace: Int) = Running(Type.TIME, goal, pace)
        fun distanceRunning(goal: Int, pace: Int) = Running(Type.DISTANCE, goal, pace)
        fun timeJogging(goal: Int) = Jogging(Type.TIME, goal)
        fun distanceJogging(goal: Int) = Jogging(Type.DISTANCE, goal)

        val WARM_UP = timeJogging(Duration.ofMinutes(5).seconds.toInt())
        val COOL_DOWN = timeJogging(Duration.ofMinutes(5).seconds.toInt())
    }

    enum class Type {
        TIME,
        DISTANCE;
    }
}
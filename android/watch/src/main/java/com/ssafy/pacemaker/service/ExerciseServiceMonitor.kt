package com.ssafy.pacemaker.service

import androidx.health.services.client.data.ExerciseUpdate
import com.ssafy.pacemaker.data.ExerciseClientManager
import com.ssafy.pacemaker.data.ExerciseMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseServiceMonitor @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val exerciseClientManager: ExerciseClientManager,
) {
    val exerciseServiceState = MutableStateFlow(
        ExerciseServiceState(
            exerciseState = null,
            exerciseMetrics = ExerciseMetrics()
        )
    )

    fun connect() {
        coroutineScope.launch {
            exerciseClientManager.exerciseUpdateFlow.collect {
                when (it) {
                    is ExerciseMessage.ExerciseUpdateMessage ->
                        processExerciseUpdate(it.exerciseUpdate)

                    is ExerciseMessage.LapSummaryMessage ->
                        exerciseServiceState.update { oldState ->
                            oldState.copy(
                                exerciseLaps = it.lapSummary.lapCount
                            )
                        }

                    is ExerciseMessage.LocationAvailabilityMessage ->
                        exerciseServiceState.update { oldState ->
                            oldState.copy(
                                locationAvailability = it.locationAvailability
                            )
                        }
                }
            }
        }
    }

    private fun processExerciseUpdate(exerciseUpdate: ExerciseUpdate) {
        exerciseServiceState.update { old ->
            old.copy(
                exerciseState = exerciseUpdate.exerciseStateInfo.state,
                exerciseMetrics = old.exerciseMetrics.update(exerciseUpdate.latestMetrics),
                activeDurationCheckpoint = exerciseUpdate.activeDurationCheckpoint
                    ?: old.activeDurationCheckpoint,
                exerciseGoal = exerciseUpdate.latestAchievedGoals
            )
        }
    }

    fun disconnect() {
        exerciseServiceState.update {
            ExerciseServiceState(
                exerciseState = null,
                exerciseMetrics = ExerciseMetrics()
            )
        }
    }
}
package com.ssafy.pacemaker.presentation.exercise

import androidx.health.services.client.data.ExerciseState
import com.ssafy.pacemaker.data.ServiceState
import com.ssafy.pacemaker.service.ExerciseServiceState

data class ExerciseScreenState(
    val hasExerciseCapabilities: Boolean,
    val isTrackingAnotherExercise: Boolean,
    val serviceState: ServiceState,
    val exerciseState: ExerciseServiceState?
) {
    val isEnding: Boolean
        get() = exerciseState?.exerciseState?.isEnding == true

    val isEnded: Boolean
        get() = exerciseState?.exerciseState?.isEnded == true

    val isPausing: Boolean
        get() = exerciseState?.exerciseState == ExerciseState.USER_PAUSING || exerciseState?.exerciseState == ExerciseState.AUTO_PAUSING || exerciseState?.exerciseState?.isResuming == true

    val isPaused: Boolean
        get() = exerciseState?.exerciseState?.isPaused == true

    val error: String?
        get() = when(serviceState) {
            is ServiceState.Connected -> serviceState.exerciseServiceState.error
            else -> null
        }
}
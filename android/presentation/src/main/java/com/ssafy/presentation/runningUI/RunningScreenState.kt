package com.ssafy.presentation.runningUI

import com.ssafy.presentation.core.exercise.ExerciseServiceState
import com.ssafy.presentation.core.exercise.ServiceState

data class RunningScreenState(
    val serviceState: ServiceState,
    val exerciseState: ExerciseServiceState?
) {
    val isEnding: Boolean
        get() = exerciseState?.exerciseState?.isEnding == true

    val isEnded: Boolean
        get() = exerciseState?.exerciseState?.isEnded == true

    val isPaused: Boolean
        get() = exerciseState?.exerciseState?.isPaused == true

    val isResuming: Boolean
        get() = exerciseState?.exerciseState?.isResuming == true

    val error: String?
        get() = when (serviceState) {
            is ServiceState.Connected -> serviceState.exerciseServiceState.error
            else -> null
        }
}

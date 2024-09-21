package com.ssafy.watch.presentation.exercise

import com.ssafy.watch.data.ServiceState
import com.ssafy.watch.service.ExerciseServiceState

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

    val isPaused: Boolean
        get() = exerciseState?.exerciseState?.isPaused == true

    val error: String?
        get() = when(serviceState) {
            is ServiceState.Connected -> serviceState.exerciseServiceState.error
            else -> null
        }
}
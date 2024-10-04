@file:OptIn(ExperimentalHorologistApi::class)

package com.ssafy.pacemaker.presentation.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.health.composables.ActiveDurationText
import com.ssafy.pacemaker.data.ServiceState
import com.ssafy.pacemaker.presentation.component.button.PauseButton
import com.ssafy.pacemaker.presentation.component.button.ResumeButton
import com.ssafy.pacemaker.presentation.component.button.StopButton
import com.ssafy.pacemaker.presentation.component.text.CadenceText
import com.ssafy.pacemaker.presentation.component.text.CaloriesText
import com.ssafy.pacemaker.presentation.component.text.DistanceText
import com.ssafy.pacemaker.presentation.component.text.DurationText
import com.ssafy.pacemaker.presentation.component.text.HRText
import com.ssafy.pacemaker.service.ExerciseServiceState

@Composable
fun ExerciseRoute(
    modifier: Modifier = Modifier,
    onDisconnected: () -> Unit,
    onEnd: () -> Unit
) {
    val viewModel = hiltViewModel<ExerciseViewModel>()
    viewModel.collectServiceState(onDisconnected, onEnd)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExerciseScreen(
        onPauseClick = { viewModel.pauseExercise() },
        onEndClick = { viewModel.endExercise() },
        onResumeClick = { viewModel.resumeExercise() },
        uiState = uiState,
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ExerciseScreen(
    onPauseClick: () -> Unit,
    onEndClick: () -> Unit,
    onResumeClick: () -> Unit,
    uiState: ExerciseScreenState,
) {
    val lastActiveDurationCheckpoint = uiState.exerciseState?.activeDurationCheckpoint
    val exerciseState = uiState.exerciseState?.exerciseState

    Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize()) {
        SpaceAroundRow {
            if (lastActiveDurationCheckpoint != null && exerciseState != null) {
                ActiveDurationText(
                    checkpoint = lastActiveDurationCheckpoint,
                    state = exerciseState
                ) {
                    DurationText(duration = it)
                }
            } else {
                DurationText(null)
            }
        }
        SpaceAroundRow {
            HRText(uiState.exerciseState?.exerciseMetrics?.heartRate)
            CaloriesText(uiState.exerciseState?.exerciseMetrics?.calories)
        }
        SpaceAroundRow {
            DistanceText(uiState.exerciseState?.exerciseMetrics?.distance)
            CadenceText(uiState.exerciseState?.exerciseMetrics?.cadence)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isPausing || uiState.isPaused) {
                ResumeButton(onResumeClick)
                StopButton(onEndClick)
            } else {
                PauseButton(onPauseClick)
            }
        }
    }
}

@Composable
private fun SpaceAroundRow(
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true
)
@Composable
fun ExerciseRoutePreview() {
    ExerciseScreen(
        onPauseClick = { },
        onEndClick = { },
        onResumeClick = { },
        uiState = ExerciseScreenState(
            hasExerciseCapabilities = false,
            isTrackingAnotherExercise = false,
            serviceState = ServiceState.Connected(
                ExerciseServiceState()
            ),
            exerciseState = ExerciseServiceState()
        )
    )
}
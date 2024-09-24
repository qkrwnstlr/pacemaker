package com.ssafy.pacemaker.presentation

import androidx.compose.runtime.Composable
import com.google.android.horologist.compose.ambient.AmbientAware
import com.ssafy.pacemaker.presentation.exercise.ExerciseRoute
import com.ssafy.pacemaker.presentation.theme.PaceMakerTheme

@Composable
fun WearApp() {
    PaceMakerTheme {
        AmbientAware(true) {
            ExerciseRoute()
        }
    }
}
package com.ssafy.watch.presentation

import androidx.compose.runtime.Composable
import com.google.android.horologist.compose.ambient.AmbientAware
import com.ssafy.watch.presentation.exercise.ExerciseRoute
import com.ssafy.watch.presentation.theme.PaceMakerTheme

@Composable
fun WearApp() {
    PaceMakerTheme {
        AmbientAware(true) {
            ExerciseRoute()
        }
    }
}
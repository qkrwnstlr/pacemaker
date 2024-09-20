package com.ssafy.watch.presentation

import androidx.compose.runtime.Composable
import com.ssafy.watch.presentation.exercise.ExerciseRoute
import com.ssafy.watch.presentation.theme.PaceMakerTheme

@Composable
fun WearApp() {
    PaceMakerTheme {
        ExerciseRoute()
    }
}
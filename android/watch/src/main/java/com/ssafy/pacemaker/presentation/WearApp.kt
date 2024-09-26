package com.ssafy.pacemaker.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.google.android.horologist.compose.ambient.AmbientAware
import com.ssafy.pacemaker.presentation.theme.PaceMakerTheme

@Composable
fun WearApp(navController: NavHostController) {
    val currentScreen by navController.currentBackStackEntryAsState()
    val isAlwaysOnScreen = currentScreen?.destination?.route in AlwaysOnRoutes

    PaceMakerTheme {
        AmbientAware(isAlwaysOnScreen) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
            )
        }
    }
}

val AlwaysOnRoutes = listOf(Screen.Exercise.route)

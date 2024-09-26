package com.ssafy.pacemaker.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.pacemaker.presentation.exercise.ExerciseRoute
import com.ssafy.pacemaker.presentation.home.HomeRoute

@Composable
fun NavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeRoute(
                onStart = { navController.navigate(Screen.Exercise.route) }
            )
        }

        composable(Screen.Exercise.route) {
            ExerciseRoute()
        }
    }
}
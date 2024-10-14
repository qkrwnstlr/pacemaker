package com.ssafy.pacemaker.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.pacemaker.presentation.exercise.ExerciseRoute
import com.ssafy.pacemaker.presentation.home.HomeRoute
import com.ssafy.pacemaker.presentation.result.ResultRoute

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
            HomeRoute()
        }

        composable(Screen.Exercise.route) {
            ExerciseRoute()
        }

        composable(Screen.Result.route) {
            ResultRoute()
        }
    }
}
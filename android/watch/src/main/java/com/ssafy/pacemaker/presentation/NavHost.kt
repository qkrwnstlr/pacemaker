package com.ssafy.pacemaker.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
            HomeRoute(
                onStart = { navController.navigateToTopLevel(Screen.Exercise) }
            )
        }

        composable(Screen.Exercise.route) {
            ExerciseRoute(
                onDisconnected = { navController.popBackStack() },
                onEnd = { navController.navigateToTopLevel(Screen.Result) }
            )
        }

        composable(Screen.Result.route) {
            ResultRoute(
                onFinish = { navController.navigateToTopLevel(Screen.Home) }
            )
        }
    }
}

fun NavController.navigateToTopLevel(screen: Screen, route: String = screen.route) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}
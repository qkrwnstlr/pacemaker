package com.ssafy.pacemaker.presentation

import androidx.health.services.client.data.ExerciseState
import androidx.navigation.NavController
import com.ssafy.pacemaker.data.HealthServicesRepository
import com.ssafy.pacemaker.data.ServiceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExerciseNavigator @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository,
) {
    fun connect(navController: NavController) {
        CoroutineScope(Dispatchers.Main).launch {
            healthServicesRepository.serviceState.collect {
                when (it) {
                    is ServiceState.Disconnected -> navController.navigateToTopLevel(Screen.Home)
                    is ServiceState.Connected -> {
                        when (it.exerciseServiceState.exerciseState) {
                            ExerciseState.ENDED -> navController.navigateToTopLevel(Screen.Result)
                            else -> navController.navigateToTopLevel(Screen.Exercise)
                        }
                    }
                }
            }
        }
    }
}


fun NavController.navigateToTopLevel(screen: Screen, route: String = screen.route) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}
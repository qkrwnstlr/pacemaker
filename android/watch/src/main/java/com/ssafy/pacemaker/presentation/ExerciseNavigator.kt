package com.ssafy.pacemaker.presentation

import android.util.Log
import androidx.health.services.client.data.ExerciseState
import androidx.navigation.NavController
import com.ssafy.pacemaker.data.HealthServicesRepository
import com.ssafy.pacemaker.data.ServiceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ExerciseNavigator_PACEMAKER"

class ExerciseNavigator @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository,
) {
    private var navigationJob: Job? = null
    fun connect(navController: NavController) {
        if (navigationJob != null) return
        navigationJob = CoroutineScope(Dispatchers.Main).launch {
            healthServicesRepository.serviceState.collect {
                Log.d(TAG, "connect: $it")
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

    fun disconnect() {
        navigationJob?.cancel()
        navigationJob = null
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
package com.ssafy.presentation.core.navigator

import androidx.navigation.NavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.exercise.ExerciseRepository
import com.ssafy.presentation.core.exercise.ServiceState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class ExerciseNavigator @Inject constructor(private val exerciseRepository: ExerciseRepository) {
    private var isConnected = false

    fun connect(navController: NavController) {
        if (isConnected) return
        isConnected = true
        CoroutineScope(Dispatchers.Main).launch {
            exerciseRepository.serviceState.takeWhile { isConnected }.collect {
                if (navController.currentDestination?.id != R.id.loginFragment) {
                    when (it) {
                        is ServiceState.Disconnected -> {
                            if (navController.currentDestination?.id == R.id.runningFragment) {
                                navController.popBackStack()
                                navController.navigate(R.id.scheduleFragment)
                            }
                        }

                        is ServiceState.Connected -> {
                            if (navController.currentDestination?.id != R.id.runningFragment) {
                                navController.navigate(R.id.runningFragment)
                            }
                        }
                    }
                }
            }
        }
    }

    fun disconnect() {
        isConnected = false
    }
}
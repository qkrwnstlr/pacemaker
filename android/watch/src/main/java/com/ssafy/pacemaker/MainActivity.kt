package com.ssafy.pacemaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.ssafy.pacemaker.presentation.Screen
import com.ssafy.pacemaker.presentation.WearApp
import com.ssafy.pacemaker.presentation.exercise.ExerciseViewModel
import com.ssafy.pacemaker.service.ExerciseService
import com.ssafy.pacemaker.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    private val exerciseViewModel by viewModels<ExerciseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        var pendingNavigation = true

        splash.setKeepOnScreenCondition { pendingNavigation }

        super.onCreate(savedInstanceState)

        PermissionHelper(this, PERMISSIONS, ::finish).launchPermission()

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            navController = rememberSwipeDismissableNavController()

            WearApp(navController)

            LaunchedEffect(Unit) {
                prepareIfNoExercise()
                pendingNavigation = false
            }
        }

        handleIntent(intent)
    }

    private suspend fun prepareIfNoExercise() {
        val isRegularLaunch = navController.currentDestination?.route == Screen.Home.route
        if (isRegularLaunch && exerciseViewModel.isExerciseInProgress()) {
            navController.navigate(Screen.Exercise.route)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            RUNNING_ACTION -> {
                Intent(this, ExerciseService::class.java).apply {
                    action = (ExerciseService.RUNNING_ACTION)
                }.run {
                    startForegroundService(this)
                }
            }
        }
    }

    companion object {
        const val RUNNING_ACTION = "com.ssafy.pacemaker.action.running"
    }
}
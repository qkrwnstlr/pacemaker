package com.ssafy.pacemaker

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.ssafy.pacemaker.presentation.ExerciseNavigator
import com.ssafy.pacemaker.presentation.WearApp
import com.ssafy.pacemaker.service.ExerciseService
import com.ssafy.pacemaker.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var exerciseNavigator: ExerciseNavigator

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        PermissionHelper(this, PERMISSIONS, ::finish).launchPermission()

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            navController = rememberSwipeDismissableNavController()

            WearApp(navController)
        }

        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if (::navController.isInitialized) exerciseNavigator.connect(navController)
    }

    override fun onPause() {
        super.onPause()
        if (::navController.isInitialized) exerciseNavigator.disconnect()
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
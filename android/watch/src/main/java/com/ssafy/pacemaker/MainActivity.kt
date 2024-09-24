package com.ssafy.pacemaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ssafy.pacemaker.presentation.WearApp
import com.ssafy.pacemaker.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        PermissionHelper(this, PERMISSIONS, ::finish).launchPermission()

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            RUNNING_ACTION -> {}
        }
    }

    companion object {
        const val RUNNING_ACTION = "com.ssafy.pacemaker.action.running"
    }
}
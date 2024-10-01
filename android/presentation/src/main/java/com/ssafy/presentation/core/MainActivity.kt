package com.ssafy.presentation.core

import android.Manifest
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.ssafy.presentation.R
import com.ssafy.presentation.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val permissions = mutableListOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        PermissionHelper(this, permissions, ::finish).launchPermission()

        handleIntent(intent)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val currentFocus = currentFocus ?: return super.dispatchTouchEvent(ev)
        val rect = Rect()
        currentFocus.getGlobalVisibleRect(rect)
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        if (rect.contains(x, y)) return super.dispatchTouchEvent(ev)

        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { imm.hideSoftInputFromWindow(currentFocus.windowToken, 0) }

        currentFocus.clearFocus()
        return super.dispatchTouchEvent(ev)
    }

    private fun handleIntent(intent: Intent?) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        val navController = navHostFragment.navController
        when (intent?.action) {
            RUNNING_ACTION -> {
                if(navController.currentDestination?.id != R.id.runningFragment) navController.navigate(R.id.runningFragment)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    companion object {
        const val RUNNING_ACTION = "com.ssafy.pacemaker.action.running"
    }
}
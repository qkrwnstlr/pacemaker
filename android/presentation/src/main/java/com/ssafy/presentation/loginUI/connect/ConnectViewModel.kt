package com.ssafy.presentation.loginUI.connect

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.ssafy.presentation.core.healthConnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
) : ViewModel() {
    suspend fun hasAllPermissions() = healthConnectManager.hasAllPermissions()
    fun launchPermissionsLauncher(fragment: Fragment) = healthConnectManager.launchPermissionsLauncher(fragment)
}
package com.ssafy.presentation.myPageUI.profile

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.presentation.core.healthConnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val healthConnectManager: HealthConnectManager,
) : ViewModel() {

    private val _userInfo: MutableStateFlow<User> = MutableStateFlow(User())
    val userInfo: StateFlow<User> = _userInfo.asStateFlow()

    fun getUserInfo(
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { _userInfo.emit(dataStoreRepository.getUser()) }
    }

    suspend fun hasAllPermissions() = healthConnectManager.hasAllPermissions()
    fun requestPermissionsActivityContract() = healthConnectManager.requestPermissionsActivityContract()
    fun launchPermissionsLauncher(activityResultLauncher: ActivityResultLauncher<Set<String>>) = healthConnectManager.launchPermissionsLauncher(activityResultLauncher)
}

package com.ssafy.presentation.loginUI.connect

import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.usecase.user.GetUserInfoUseCase
import com.ssafy.domain.usecase.user.ModifyUserUseCase
import com.ssafy.presentation.core.healthConnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val dataStoreRepository: DataStoreRepository,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val modifyUserUseCase: ModifyUserUseCase
) : ViewModel() {
    suspend fun hasAllPermissions() = healthConnectManager.hasAllPermissions()

    fun requestPermissionsActivityContract() =
        healthConnectManager.requestPermissionsActivityContract()

    fun launchPermissionsLauncher(activityResultLauncher: ActivityResultLauncher<Set<String>>) =
        healthConnectManager.launchPermissionsLauncher(activityResultLauncher)

    suspend fun syncWithHealthConnect(uid: String) {
        val userInfo = getUserInfoUseCase(uid)
        val start = ZonedDateTime.now()
        val end = start.minusDays(30)
        val height = healthConnectManager.readHeightInputs(end.toInstant(), start.toInstant())
        val weight = healthConnectManager.readHeightInputs(end.toInstant(), start.toInstant())
        val user = userInfo.copy(
            height = height?.toInt() ?: userInfo.height,
            weight = weight?.toInt() ?: userInfo.weight,
        )
        dataStoreRepository.saveUser(user)
        modifyUserUseCase(uid, user)
    }
}
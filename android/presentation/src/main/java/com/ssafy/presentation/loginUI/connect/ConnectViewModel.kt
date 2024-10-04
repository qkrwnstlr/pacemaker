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

    fun requestPermissionsActivityContract() = healthConnectManager.requestPermissionsActivityContract()

    suspend fun launchPermissionsLauncher(activityResultLauncher: ActivityResultLauncher<Set<String>>, onNotAbleAction: () -> Unit) =
        healthConnectManager.launchPermissionsLauncher(activityResultLauncher, onNotAbleAction)

    suspend fun syncWithHealthConnect(uid: String) {
        val userInfo = getUserInfoUseCase(uid)
        val end = ZonedDateTime.now()
        val start = end.minusDays(30)
        val height = healthConnectManager.readHeightInputs(start.toInstant(), end.toInstant())?.run { this * 100 }
        val weight = healthConnectManager.readWeightInputs(start.toInstant(), end.toInstant())

        val user = userInfo.copy(
            height = height?.toInt() ?: userInfo.height,
            weight = weight?.toInt() ?: userInfo.weight,
        )
        dataStoreRepository.saveUser(user)
        modifyUserUseCase(uid, user)
    }
}
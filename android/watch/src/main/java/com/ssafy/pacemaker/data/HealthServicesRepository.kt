package com.ssafy.pacemaker.data

import android.content.Context
import android.util.Log
import com.ssafy.pacemaker.di.bindService
import com.ssafy.pacemaker.service.ExerciseService
import com.ssafy.pacemaker.service.ExerciseServiceState
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HealthServicesRepository_PACEMAKER"

@ActivityRetainedScoped
class HealthServicesRepository @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val exerciseClientManager: ExerciseClientManager,
    private val wearableClientManager: WearableClientManager,
    private val coroutineScope: CoroutineScope,
    lifecycle: ActivityRetainedLifecycle
) {
    private val binderConnection =
        lifecycle.bindService<ExerciseService.LocalBinder, ExerciseService>(applicationContext)

    private val exerciseServiceStateUpdates: Flow<ExerciseServiceState> =
        binderConnection.flowWhenConnected(ExerciseService.LocalBinder::exerciseServiceState)

    private var errorState: MutableStateFlow<String?> = MutableStateFlow(null)

    val serviceState: StateFlow<ServiceState> =
        exerciseServiceStateUpdates.combine(errorState) { exerciseServiceState, errorString ->
            wearableClientManager.sendToMobileDevice(
                WearableClientManager.EXERCISE_DATA_PATH,
                exerciseServiceState
            )

            ServiceState.Connected(exerciseServiceState.copy(error = errorString))
        }.stateIn(
            coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = ServiceState.Disconnected
        )

    suspend fun hasExerciseCapability(): Boolean = getExerciseCapabilities() != null

    private suspend fun getExerciseCapabilities() = exerciseClientManager.getExerciseCapabilities()

    suspend fun isExerciseInProgress(): Boolean = exerciseClientManager.isExerciseInProgress()

    suspend fun isTrackingExerciseInAnotherApp(): Boolean =
        exerciseClientManager.isTrackingExerciseInAnotherApp()

    private fun serviceCall(function: suspend ExerciseService.() -> Unit) = coroutineScope.launch {
        binderConnection.runWhenConnected {
            function(it.getService())
        }
    }

    fun prepareExercise() = serviceCall { prepareExercise() }

    fun startExercise() = serviceCall {
        try {
            errorState.value = null
            Log.d(TAG, "startExercise: ")
            startExercise()
        } catch (e: Exception) {
            errorState.value = e.message
        }
    }

    fun pauseExercise() = serviceCall { pauseExercise() }
    fun endExercise() = serviceCall { endExercise() }
    fun resumeExercise() = serviceCall { resumeExercise() }
}

sealed class ServiceState {
    data object Disconnected : ServiceState()

    data class Connected(val exerciseServiceState: ExerciseServiceState) : ServiceState()
}







package com.ssafy.presentation.core.exercise

import android.content.Context
import androidx.health.services.client.data.ExerciseState
import com.ssafy.presentation.utils.BinderConnection
import com.ssafy.presentation.utils.bindService
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class ExerciseRepository @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val coroutineScope: CoroutineScope,
    private val lifecycle: ActivityRetainedLifecycle
) {
    private var binderConnection: BinderConnection<ExerciseService.LocalBinder>? = null

    private var exerciseServiceStateUpdates: Flow<ExerciseServiceState>? = null

    val serviceState: MutableStateFlow<ServiceState> = MutableStateFlow(ServiceState.Disconnected)

    private fun bindService() {
        binderConnection = lifecycle.bindService<ExerciseService.LocalBinder, ExerciseService>(applicationContext)

        exerciseServiceStateUpdates = binderConnection?.flowWhenConnected(ExerciseService.LocalBinder::exerciseServiceState)

        coroutineScope.launch {
            exerciseServiceStateUpdates?.takeWhile { binderConnection != null }?.collect { exerciseServiceState ->
                serviceState.update { ServiceState.Connected(exerciseServiceState) }
                if (exerciseServiceState.exerciseState == ExerciseState.ENDED) unbindService()
            }
        }
    }

    private fun unbindService() {
        binderConnection?.unbind()
        binderConnection = null
        exerciseServiceStateUpdates = null
        serviceState.update { ServiceState.Disconnected }
    }

    private fun serviceCall(function: suspend ExerciseService.() -> Unit) =
        coroutineScope.launch(Dispatchers.Main) {
            binderConnection?.runWhenConnected {
                function(it.getService())
            }
        }

    fun startExercise() = bindService()
    fun pauseExercise() = serviceCall { pauseExercise() }
    fun resumeExercise() = serviceCall { resumeExercise() }
    fun endExercise() = serviceCall { endExercise() }
}

sealed class ServiceState {
    data object Disconnected : ServiceState()

    data class Connected(val exerciseServiceState: ExerciseServiceState) : ServiceState()
}
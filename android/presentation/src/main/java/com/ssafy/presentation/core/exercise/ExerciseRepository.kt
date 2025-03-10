package com.ssafy.presentation.core.exercise

import android.content.Context
import androidx.health.services.client.data.ExerciseState
import com.ssafy.presentation.core.exercise.manager.TrainState
import com.ssafy.presentation.utils.BinderConnection
import com.ssafy.presentation.utils.bindService
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private var trainStateUpdates: Flow<TrainState>? = null

    val serviceState: MutableStateFlow<ServiceState> = MutableStateFlow(ServiceState.Disconnected)
    val trainState: MutableStateFlow<TrainState> = MutableStateFlow(TrainState.None)

    private var exerciseServiceStateJob: Job? = null
    private var trainStateJob: Job? = null
    private var unbindServiceJob: Job? = null

    private fun bindService() {
        if (binderConnection != null) return

        binderConnection =
            lifecycle.bindService<ExerciseService.LocalBinder, ExerciseService>(applicationContext)

        exerciseServiceStateUpdates =
            binderConnection?.flowWhenConnected(ExerciseService.LocalBinder::exerciseServiceState)
        trainStateUpdates =
            binderConnection?.flowWhenConnected(ExerciseService.LocalBinder::trainState)

        exerciseServiceStateJob = coroutineScope.launch {
            exerciseServiceStateUpdates?.collect { exerciseServiceState ->
                serviceState.update { ServiceState.Connected(exerciseServiceState) }
                if (exerciseServiceState.exerciseState == ExerciseState.ENDED) unbindService()
            }
            this.cancel()
        }

        trainStateJob = coroutineScope.launch {
            trainStateUpdates?.collect { state ->
                trainState.update { state }
            }
            this.cancel()
        }
    }

    private fun unbindService() {
        unbindServiceJob = coroutineScope.launch {
            trainStateUpdates?.collect { state ->
                if (state == TrainState.None) {
                    binderConnection?.unbind()
                    binderConnection = null
                    exerciseServiceStateUpdates = null
                    trainStateUpdates = null

                    exerciseServiceStateJob?.cancel()
                    trainStateJob?.cancel()
                    unbindServiceJob?.cancel()
                    exerciseServiceStateJob = null
                    trainStateJob = null
                    unbindServiceJob = null

                    serviceState.update { ServiceState.Disconnected }
                }
            }
        }
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

    fun skipWarmUp() = serviceCall { skipWarmUp() }
    fun skipCoolDown() = serviceCall { skipCoolDown() }
}

sealed class ServiceState {
    data object Disconnected : ServiceState()

    data class Connected(val exerciseServiceState: ExerciseServiceState) : ServiceState()
}
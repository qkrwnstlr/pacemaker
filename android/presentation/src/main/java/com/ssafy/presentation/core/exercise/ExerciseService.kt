package com.ssafy.presentation.core.exercise

import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.health.services.client.data.ExerciseGoal
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate.ActiveDurationCheckpoint
import androidx.health.services.client.data.LocationAvailability
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ssafy.domain.usecase.train.GetTTSUseCase
import com.ssafy.presentation.core.exercise.data.ExerciseMetrics
import com.ssafy.presentation.core.exercise.data.message
import com.ssafy.presentation.core.exercise.manager.CoachingManager
import com.ssafy.presentation.core.exercise.manager.ExerciseManager
import com.ssafy.presentation.core.exercise.manager.ExerciseNotificationManager
import com.ssafy.presentation.core.exercise.manager.ReportsManager
import com.ssafy.presentation.core.exercise.manager.TrainManager
import com.ssafy.presentation.core.exercise.manager.TrainSession
import com.ssafy.presentation.core.exercise.manager.TrainState
import com.ssafy.presentation.core.exercise.manager.WearableClientManager
import com.ssafy.presentation.core.healthConnect.HealthConnectManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class ExerciseService : LifecycleService() {
    @Inject
    lateinit var exerciseNotificationManager: ExerciseNotificationManager

    @Inject
    lateinit var wearableClientManager: WearableClientManager

    @Inject
    lateinit var exerciseManager: ExerciseManager

    @Inject
    lateinit var coachingManager: CoachingManager

    @Inject
    lateinit var reportsManager: ReportsManager

    @Inject
    lateinit var trainManager: TrainManager

    @Inject
    lateinit var healthConnectManager: HealthConnectManager

    @Inject
    lateinit var getTTSUseCase: GetTTSUseCase


    private var isBound = false
    private var isStarted = false
    private val localBinder = LocalBinder()
    private val mediaPlayer by lazy { MediaPlayer() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (!isStarted) {
            isStarted = true
            startForeground()
        }

        return START_STICKY
    }

    private suspend fun connectToTrainManager() {
        trainManager.connect(exerciseManager.currentSessionData)
        trainManager.trainState.collect {
            speakMessage(it.message)

            when (it) {
                TrainState.Before -> {
                    startExercise()
                }

                is TrainState.WarmUp -> {
                    exerciseManager.connect()
                    speakMessage(trainManager.train.message)
                }

                is TrainState.During -> {
                    when (it.session) {
                        is TrainSession.Running -> {
                            exerciseManager.stopJogging()
                            exerciseManager.startRunning()
                            coachingManager.connect(trainManager.train)
                        }

                        is TrainSession.Jogging -> {
                            exerciseManager.stopRunning()
                            exerciseManager.startJogging()
                            coachingManager.disconnect()
                        }
                    }
                }

                is TrainState.CoolDown -> {
                    coachingManager.disconnect()
                }

                TrainState.Ended -> {
                    endExercise()
                }
            }
        }
    }

    private suspend fun collectExerciseServiceState() {
        exerciseManager.exerciseServiceState.collect {
            when (it.exerciseState) {
                ExerciseState.ENDED -> {
                    healthConnectManager.writeExerciseSession(
                        "${trainManager.train.id} (#${trainManager.train.index})",
                        exerciseManager.exerciseData.value,
                    )
                    reportsManager.createPlanReports(
                        trainManager.train.id,
                        exerciseManager.exerciseData.value,
                    )
                    exerciseManager.disconnect()
                    coachingManager.disconnect()
                }
            }
        }
    }

    private suspend fun collectCoachVoicePath() {
        coachingManager.coachVoicePath.collect { coachPath ->
            speakCoaching(coachPath)
        }
    }

    private suspend fun speakMessage(message: String) {
        runCatching {
            getTTSUseCase(message)
        }.onSuccess { path ->
            speakCoaching(path)
        }
    }

    private fun speakCoaching(coachPath: String) {
        val file = File(coachPath)
        if (!file.exists()) return

        mediaPlayer.apply {
            setDataSource(file.path)
            prepare()
            start()

            setOnCompletionListener { mp ->
                mp.release()
                file.delete()
            }
        }
    }

    private fun stopSelfIfNotRunning() {
        lifecycleScope.launch {
            if (exerciseManager.exerciseServiceState.value.exerciseState == ExerciseState.PREPARING) {
                exerciseManager.disconnect()
                coachingManager.disconnect()
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
            stopSelf()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)

        handleBind()

        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)

        handleBind()
    }

    private fun handleBind() {
        if (!isBound) {
            isBound = true
            startForegroundService(Intent(this, this::class.java))
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        lifecycleScope.launch {
            delay(UNBIND_DELAY)
            if (!isBound) stopSelfIfNotRunning()
        }
        return true
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@ExerciseService

        val exerciseServiceState: Flow<ExerciseServiceState>
            get() = this@ExerciseService.exerciseManager.exerciseServiceState

        val coachVoicePathState: Flow<String>
            get() = this@ExerciseService.coachingManager.coachVoicePath
    }

    private fun startForeground() {
        exerciseNotificationManager.createNotificationChannel()
        val notification = exerciseNotificationManager.buildNotification()

        ServiceCompat.startForeground(
            this,
            ExerciseNotificationManager.NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            else
                0
        )
    }

    suspend fun startExercise() {
        connectToTrainManager()
        collectExerciseServiceState()
        collectCoachVoicePath()
        wearableClientManager.startWearableActivity()
        wearableClientManager.sendToWearableDevice(WearableClientManager.START_RUN_PATH)
    }

    suspend fun pauseExercise() {
        wearableClientManager.sendToWearableDevice(WearableClientManager.PAUSE_RUN_PATH)
    }

    suspend fun resumeExercise() {
        wearableClientManager.sendToWearableDevice(WearableClientManager.RESUME_RUN_PATH)
    }

    suspend fun endExercise() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        wearableClientManager.sendToWearableDevice(WearableClientManager.END_RUN_PATH)
    }

    companion object {
        private val UNBIND_DELAY = 3.seconds
    }
}

data class ExerciseServiceState(
    val exerciseState: ExerciseState? = null,
    val exerciseMetrics: ExerciseMetrics = ExerciseMetrics(),
    val exerciseLaps: Int = 0,
    val activeDurationCheckpoint: ActiveDurationCheckpoint? = null,
    val locationAvailability: LocationAvailability = LocationAvailability.UNKNOWN,
    val error: String? = null,
    val exerciseGoal: Set<ExerciseGoal<out Number>> = emptySet()
)
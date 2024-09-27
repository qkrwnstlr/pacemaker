package com.ssafy.presentation.core.exercise

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ServiceCompat
import androidx.health.services.client.data.ExerciseGoal
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseUpdate.ActiveDurationCheckpoint
import androidx.health.services.client.data.LocationAvailability
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ssafy.domain.dto.train.CoachingResponse
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.presentation.core.healthConnect.HealthConnectManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ExerciseService_PACEMAKER"

@AndroidEntryPoint
class ExerciseService : LifecycleService() {
    @Inject
    lateinit var exerciseNotificationManager: ExerciseNotificationManager

    @Inject
    lateinit var wearableClientManager: WearableClientManager

    @Inject
    lateinit var exerciseMonitor: ExerciseMonitor

    @Inject
    lateinit var coachingManager: CoachingManager

    @Inject
    lateinit var reportsManager: ReportsManager

    @Inject
    lateinit var healthConnectManager: HealthConnectManager

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    private var isBound = false
    private var isStarted = false
    private val localBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (!isStarted) {
            isStarted = true
            Log.d(TAG, "onStartCommand: ")
            startForeground()
        }

        return START_STICKY
    }

    private fun connectToExerciseMonitor() {
        lifecycleScope.launch(Dispatchers.Default) {
            exerciseMonitor.connect()
            exerciseMonitor.exerciseServiceState.collect { exercise ->
                if (exercise.exerciseState == ExerciseState.ENDED) {
                    val result = healthConnectManager.writeExerciseSession(
                        // TODO : title 변경
                        "My Run #${Random.nextInt(0, 60)}",
                        parseExerciseData(
                            exercise.exerciseMetrics,
                            exerciseMonitor.exerciseSessionData.value
                        ),
                        exerciseMonitor.exerciseSessionData.value
                    )
                    Log.d(TAG, "connectToExerciseMonitor: ${result.recordIdsList}")
                    reportsManager.createPlanReports(
                        exercise.exerciseMetrics,
                        exerciseMonitor.exerciseSessionData.value,
                        dataStoreRepository.getUser().coachNumber
                    )
                    exerciseMonitor.disconnect()
                    coachingManager.disconnect()
                }
            }
        }
    }

    private fun connectToCoachingMonitor() {
        lifecycleScope.launch(Dispatchers.Default) {
            coachingManager.connect()
            coachingManager.coachingResponse.collect { coaching ->
                // TODO : TTS로 변경
                runBlocking(Dispatchers.Main) {
                    Toast.makeText(this@ExerciseService, "$coaching", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun stopSelfIfNotRunning() {
        lifecycleScope.launch {
            if (exerciseMonitor.exerciseServiceState.value.exerciseState == ExerciseState.PREPARING) {
                exerciseMonitor.disconnect()
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
        Log.d(TAG, "handleBind: ")
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
            get() = this@ExerciseService.exerciseMonitor.exerciseServiceState

        val coachingResponseState: Flow<CoachingResponse>
            get() = this@ExerciseService.coachingManager.coachingResponse
    }

    private fun startForeground() {
        Log.d(TAG, "startForeground: ")
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
        connectToExerciseMonitor()
        connectToCoachingMonitor()
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
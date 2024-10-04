package com.ssafy.pacemaker.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ssafy.pacemaker.data.ExerciseClientManager
import com.ssafy.pacemaker.data.WearableClientManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class ExerciseService : LifecycleService() {

    @Inject
    lateinit var exerciseClientManager: ExerciseClientManager

    @Inject
    lateinit var exerciseNotificationManager: ExerciseNotificationManager

    @Inject
    lateinit var exerciseServiceMonitor: ExerciseServiceMonitor

    @Inject
    lateinit var exerciseMonitor: ExerciseMonitor

    @Inject
    lateinit var wearableClientManager: WearableClientManager

    private var isBound = false
    private var isStarted = false
    private val localBinder = LocalBinder()

    private suspend fun isExerciseInProgress() = exerciseClientManager.isExerciseInProgress()

    suspend fun prepareExercise() {
        exerciseServiceMonitor.connect()
        exerciseClientManager.prepareExercise()
    }

    suspend fun startExercise() {
        wearableClientManager.startMobileActivity()
        exerciseClientManager.startExercise()
        exerciseServiceMonitor.connect()
        exerciseMonitor.connect()
    }

    suspend fun pauseExercise() {
        exerciseClientManager.pauseExercise()
    }

    suspend fun resumeExercise() {
        exerciseClientManager.resumeExercise()
    }

    suspend fun endExercise() {
        exerciseClientManager.endExercise()
        removeOngoingActivityNotification()
    }

    fun clearExercise() {
        exerciseServiceMonitor.disconnect()
        exerciseMonitor.disconnect()
        isStarted = false
        stopSelf()
    }

    fun markLap() {
        lifecycleScope.launch {
            exerciseClientManager.markLap()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent?.action == RUNNING_ACTION && !isStarted) {
            isStarted = true

            startForeground()
            exerciseMonitor.connect()
            exerciseServiceMonitor.connect()

            lifecycleScope.launch {
                exerciseServiceMonitor.exerciseServiceState.collect { exerciseServiceState ->
                    wearableClientManager.sendToMobileDevice(
                        WearableClientManager.EXERCISE_DATA_PATH,
                        exerciseServiceState
                    )
                }
            }
        }

        if (intent?.action == RUNNING_ACTION) {
            lifecycleScope.launch { startExercise() }
        }

        return START_STICKY
    }

    private fun stopSelfIfNotRunning() {
        lifecycleScope.launch {
            if (!isExerciseInProgress()) {
                lifecycleScope.launch { endExercise() }
                stopSelf()
            }
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
            startService(Intent(this, this::class.java))
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

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch { endExercise() }
    }

    private fun removeOngoingActivityNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun startForeground() {
        exerciseNotificationManager.createNotificationChannel()
        val serviceState = exerciseServiceMonitor.exerciseServiceState.value
        val notification = exerciseNotificationManager.buildNotification(
            serviceState.activeDurationCheckpoint?.activeDuration ?: Duration.ZERO
        )
        ServiceCompat.startForeground(
            this,
            ExerciseNotificationManager.NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                    else
                        0
        )
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@ExerciseService

        val exerciseServiceState: Flow<ExerciseServiceState>
            get() = this@ExerciseService.exerciseServiceMonitor.exerciseServiceState
    }

    companion object {
        private val UNBIND_DELAY = 3.seconds
        const val RUNNING_ACTION = "com.ssafy.pacemaker.action.running"
    }
}

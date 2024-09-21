package com.ssafy.watch.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ssafy.watch.PERMISSIONS
import com.ssafy.watch.data.HealthServicesBackgroundRepository
import com.ssafy.watch.data.PassiveDataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StartupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val repository = PassiveDataRepository(context)
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        runBlocking {
            if (repository.passiveDataEnabled.first()) {
                PERMISSIONS.all {
                    context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
                }.run {
                    if (this) {
                        scheduleWorker(context)
                    } else {
                        repository.setPassiveDataEnabled(false)
                    }
                }
            }
        }
    }

    private fun scheduleWorker(context: Context) {
        WorkManager.getInstance(context).enqueue(
            OneTimeWorkRequestBuilder<RegisterForBackgroundDataWorker>().build()
        )
    }
}

class RegisterForBackgroundDataWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val healthServicesBackgroundRepository = HealthServicesBackgroundRepository(appContext)
        healthServicesBackgroundRepository.registerForHealthService()
        return Result.success()
    }
}

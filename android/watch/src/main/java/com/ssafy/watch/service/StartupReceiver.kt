/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.ssafy.watch.data.HealthServicesRepository
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
        val healthServicesRepository = HealthServicesRepository(appContext)
        healthServicesRepository.registerForHealthService()
        return Result.success()
    }
}

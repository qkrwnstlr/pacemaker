package com.ssafy.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
class GpsTracker(
    context: Context,
    params: WorkerParameters,
    val onSuccess: (Location) -> Unit,
    val onFailure: () -> Unit
) : CoroutineWorker(context, params) {
    private val mContext = context

    //위치 가져올때 필요
    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(mContext)
    }

    override suspend fun doWork(): Result {
        return try {
            mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess(task.result)
                else Result.failure()
            }
            Result.success()
        } catch (err: Exception) {
            Result.failure()
        }
    }
}
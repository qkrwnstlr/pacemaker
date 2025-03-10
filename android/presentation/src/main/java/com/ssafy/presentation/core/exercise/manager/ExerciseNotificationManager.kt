package com.ssafy.presentation.core.exercise.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ssafy.presentation.R
import com.ssafy.presentation.core.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExerciseNotificationManager @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    private val manager: NotificationManager
) {

    fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_CHANNEL_DISPLAY,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(notificationChannel)
    }

    fun buildNotification(): Notification {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java).apply {
            action = MainActivity.RUNNING_ACTION
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_TEXT)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_WORKOUT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        return notificationBuilder.build()
    }

    companion object {
        const val NOTIFICATION_ID = 100001
        private const val NOTIFICATION_CHANNEL = "com.ssafy.pacemaker.ONGOING_EXERCISE"
        private const val NOTIFICATION_CHANNEL_DISPLAY = "Ongoing Exercise"
        private const val NOTIFICATION_TITLE = "PaceMaker"
        private const val NOTIFICATION_TEXT = "Ongoing Exercise"
    }
}
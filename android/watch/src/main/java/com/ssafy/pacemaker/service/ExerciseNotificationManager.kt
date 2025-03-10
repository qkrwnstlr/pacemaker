package com.ssafy.pacemaker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.ssafy.pacemaker.MainActivity
import com.ssafy.pacemaker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
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

    fun buildNotification(duration: Duration): Notification {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
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

        val startMillis = SystemClock.elapsedRealtime() - duration.toMillis()
        val ongoingActivityStatus = Status.Builder()
            .addTemplate(ONGOING_STATUS_TEMPLATE)
            .addPart("duration", Status.StopwatchPart(startMillis))
            .build()
        val ongoingActivity =
            OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
                .setAnimatedIcon(R.drawable.logo)
                .setStaticIcon(R.drawable.logo)
                .setTouchIntent(pendingIntent)
                .setStatus(ongoingActivityStatus)
                .build()

        ongoingActivity.apply(applicationContext)

        return notificationBuilder.build()
    }

    companion object {
        const val NOTIFICATION_ID = 100001
        private const val NOTIFICATION_CHANNEL = "com.ssafy.pacemaker.ONGOING_EXERCISE"
        private const val NOTIFICATION_CHANNEL_DISPLAY = "Ongoing Exercise"
        private const val NOTIFICATION_TITLE = "PaceMaker"
        private const val NOTIFICATION_TEXT = "Ongoing Exercise"
        private const val ONGOING_STATUS_TEMPLATE = "Ongoing Exercise #duration#"
    }
}
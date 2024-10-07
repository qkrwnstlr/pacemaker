package com.ssafy.presentation.utils

import androidx.core.text.buildSpannedString
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

private val MINUTES_PER_HOUR = TimeUnit.HOURS.toMinutes(1)
private val SECONDS_PER_MINUTE = TimeUnit.MINUTES.toSeconds(1)

fun formatElapsedTime(
    elapsedDuration: Duration?,
    includeSeconds: Boolean = false
) = buildSpannedString {
    if (elapsedDuration == null) {
        append("--")
    } else {
        val hours = elapsedDuration.toHours()
        if (hours > 0) {
            append(hours.toString())
            append(" : ")
        }
        val minutes = elapsedDuration.toMinutes() % MINUTES_PER_HOUR
        append("%02d".format(minutes))
        append(" : ")
        if (includeSeconds) {
            val seconds = elapsedDuration.seconds % SECONDS_PER_MINUTE
            append("%02d".format(seconds))
        } else {
            append("00")
        }
    }
}

fun formatCalories(calories: Double?) = buildSpannedString {
    if (calories == null || calories.isNaN()) {
        append("--")
    } else {
        append(calories.roundToInt().toString())
        append(" kcal")
    }
}

fun formatDistance(meters: Double?) = buildSpannedString {
    if (meters == null) {
        append("--")
    } else {
        append("%02.2f".format(meters / 1_000))
    }
}

fun formatDistanceKm(meters: Double?) = buildSpannedString {
    if (meters == null) {
        append("--")
    } else {
        append("%02.2f".format(meters / 1_000))
        append(" km")
    }
}

fun formatHeartRate(bpm: Double?) = buildSpannedString {
    if (bpm == null || bpm.isNaN()) {
        append("--")
    } else {
        append("%.0f".format(bpm))
        append(" bpm")
    }
}

fun formatCadenceRate(cadence: Int?) = buildSpannedString {
    if (cadence == null) {
        append("--")
    } else {
        append("%d".format(cadence))
        append(" spm")
    }
}

fun formatSpeed(speed: Double?) = buildSpannedString {
    if (speed == null || speed.isNaN()) {
        append("--")
    } else {
        val secondsPerKm = 1000.0 / speed
        val minutes = (secondsPerKm / 60).toInt()
        val seconds = (secondsPerKm % 60).toInt()

        append("%02d".format(minutes))
        append("'")
        append("%02d".format(seconds))
        append('"')
    }
}

fun formatPace(pace: Double?) = buildSpannedString {
    if (pace == null || pace.isNaN()) {
        append("--")
    } else {
        val minutes = (pace / 60).toInt()
        val seconds = (pace % 60).toInt()

        append("%02d".format(minutes))
        append("'")
        append("%02d".format(seconds))
        append('"')
    }
}

package com.ssafy.pacemaker.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.wear.compose.material.MaterialTheme
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

private val MINUTES_PER_HOUR = TimeUnit.HOURS.toMinutes(1)
private val SECONDS_PER_MINUTE = TimeUnit.MINUTES.toSeconds(1)

@Composable
fun formatElapsedTime(
    elapsedDuration: Duration?,
    includeSeconds: Boolean = false
) = buildAnnotatedString {
    if (elapsedDuration == null) {
        append("--")
    } else {
        val hours = elapsedDuration.toHours()
        if (hours > 0) {
            append(hours.toString())
            withStyle(style = MaterialTheme.typography.caption3.toSpanStyle()) {
                append("h")
            }
        }
        val minutes = elapsedDuration.toMinutes() % MINUTES_PER_HOUR
        append("%02d".format(minutes))
        withStyle(style = MaterialTheme.typography.caption3.toSpanStyle()) {
            append("m")
        }
        if (includeSeconds) {
            val seconds = elapsedDuration.seconds % SECONDS_PER_MINUTE
            append("%02d".format(seconds))
            withStyle(style = MaterialTheme.typography.caption3.toSpanStyle()) {
                append("s")
            }
        }
    }
}

@Composable
fun formatCalories(calories: Double?) = buildAnnotatedString {
    if (calories == null || calories.isNaN()) {
        append("--")
    } else {
        append(calories.roundToInt().toString())
        withStyle(style = MaterialTheme.typography.caption3.toSpanStyle()) {
            append(" cal")
        }
    }
}

@Composable
fun formatDistanceKm(meters: Double?) = buildAnnotatedString {
    if (meters == null) {
        append("--")
    } else {
        append("%02.2f".format(meters / 1_000))
        withStyle(style = MaterialTheme.typography.caption3.toSpanStyle()) {
            append("km")
        }
    }
}

@Composable
fun formatHeartRate(bpm: Double?) = buildAnnotatedString {
    if (bpm == null || bpm.isNaN()) {
        append("--")
    } else {
        append("%.0f".format(bpm))
        withStyle(style = MaterialTheme.typography.caption3.toSpanStyle()) {
            append("bpm")
        }
    }
}

@Composable
fun formatPace(pace: Double?) = buildAnnotatedString {
    if (pace == null || pace.isNaN()) {
        append("--")
    } else {
        val secondsPerKm = pace / 1000.0
        val minutes = (secondsPerKm / 60).toInt()
        val seconds = (secondsPerKm % 60).toInt()

        append("%02d".format(minutes))
        append("'")
        append("%02d".format(seconds))
        append('"')
    }
}

@Composable
fun formatSpeed(speed: Double?) = buildAnnotatedString {
    if (speed == null || speed.isNaN()) {
        append("--")
    } else {
        val secondsPerKm = 1000 / speed
        val minutes = (secondsPerKm / 60).toInt()
        val seconds = (secondsPerKm % 60).toInt()

        append("%02d".format(minutes))
        append("'")
        append("%02d".format(seconds))
        append('"')
    }
}

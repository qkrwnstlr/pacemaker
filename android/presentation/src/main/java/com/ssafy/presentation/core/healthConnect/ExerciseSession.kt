package com.ssafy.presentation.core.healthConnect

import java.time.ZonedDateTime

data class ExerciseSession(
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val id: String,
    val title: String?,
    val sourceAppInfo: HealthConnectAppInfo?
)
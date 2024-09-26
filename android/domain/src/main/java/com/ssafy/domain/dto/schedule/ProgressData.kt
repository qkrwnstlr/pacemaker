package com.ssafy.domain.dto.schedule

data class ProgressData(
    val id: Long = 0,
    val goal: String = "",
    val totalDays: Int = 0,
    val completedCount: Int = 0,
    val status: String = "NOTHING"
)

package com.ssafy.domain.dto.schedule

import com.ssafy.domain.dto.plan.Context

data class ProgressData(
    val id: Long = 0,
    val totalDistances : Int = 0,
    val totalTimes: Int = 0,
    val totalDays: Int = 0,
    val completedCount: Int = 0,
    val status: String = "ACTIVE",
    val context: Context = Context()
)
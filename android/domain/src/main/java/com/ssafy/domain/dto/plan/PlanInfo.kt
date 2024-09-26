package com.ssafy.domain.dto.plan

data class PlanInfo(
    val id: Long,
    val createdAt: String,
    val expiredAt: String,
    val totalDays: Int,
    val totalTimes: Int,
    val totalDistances: Int,
    val context: Context,
    val completedCount: Int,
    val status: String,
    val planTrains: List<PlanTrain>
)

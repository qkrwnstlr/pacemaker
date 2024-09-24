package com.ssafy.domain.dto.plan

data class Plan(
    val totalDays: Int = 0,
    val totalTimes: Int = 0,
    val totalDistances: Int = 0,
    val planTrains: List<PlanTrain> = emptyList()
)

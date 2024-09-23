package com.ssafy.domain.dto.plan

import java.time.LocalDateTime

data class PlanTrain(
    val index: Int = 0,
    val trainDate: LocalDateTime = LocalDateTime.now(),
    val trainType: String = "",
    val sessionTime: Int = 0,
    val sessionDistance: Int = 0,
    val repeat: Int = 0,
    val trainParam: Int = 0,
    val trainPace: Int = 0,
    val interParam: Int = 0
)

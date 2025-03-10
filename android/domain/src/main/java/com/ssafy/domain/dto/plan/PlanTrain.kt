package com.ssafy.domain.dto.plan

data class PlanTrain(
    val id: Long = 0,
    val index: Int = 0,
    val trainDate: String = "",
    val paramType: String = "",
    val sessionTime: Int = 0,
    val sessionDistance: Int = 0,
    val repetition: Int = 0,
    val trainParam: Int = 0,
    val trainPace: Int = 0,
    val interParam: Int = 0,
    val status: String = ""
)

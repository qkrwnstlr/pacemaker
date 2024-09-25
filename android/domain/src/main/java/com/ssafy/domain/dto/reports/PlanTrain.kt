package com.ssafy.domain.dto.reports

data class PlanTrain(
    val index: Int,
    val trainDate: String,
    val paramType: String,
    val sessionTime: Int,
    val sessionDistance: Int,
    val repetition: Int,
    val trainParam: Int,
    val trainPace: Int,
    val interParam: Int
)

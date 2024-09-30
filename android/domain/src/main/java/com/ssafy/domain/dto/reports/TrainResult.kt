package com.ssafy.domain.dto.reports

data class TrainResult(
    val trainDistance: Int,
    val trainTime: Int,
    val heartRate: Int,
    val pace: Int,
    val cadence: Int,
    val kcal: Int,
    val heartZone: List<Int>,
    val splitData: List<SplitData>,
    val trainMap: List<List<Double>>,
    val coachMessage: List<String>? = listOf(),
    val coachNumber: Long? = 0L
)
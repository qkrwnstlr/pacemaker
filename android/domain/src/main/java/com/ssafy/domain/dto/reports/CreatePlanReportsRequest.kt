package com.ssafy.domain.dto.reports

data class CreatePlanReportsRequest(
    val uid: String,
    val planTrainId: Long,
    val coachNumber: Long,
    val trainDate: String,
    val trainResult: TrainResult
)

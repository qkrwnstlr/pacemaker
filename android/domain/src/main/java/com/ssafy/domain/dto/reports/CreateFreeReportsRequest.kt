package com.ssafy.domain.dto.reports

data class CreateFreeReportsRequest(
    val uid: String,
    val trainDate: String,
    val trainResult: TrainResult
)

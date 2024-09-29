package com.ssafy.domain.dto.train

import com.ssafy.domain.dto.plan.PlanTrain
import com.google.gson.annotations.SerializedName

data class CoachingRequest(
    val totalDistance: Float,
    val nowDistance: Float,
    val meanHeartRate: Int,
    val meanPace: Int,
    val meanCadence: Int,
    val planTrain: PlanTrain,
    val coachTone: String = "",
    @SerializedName("coachNumber")
    val coachIndex: Long = 2L
)
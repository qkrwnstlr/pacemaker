package com.ssafy.domain.dto.train

import com.ssafy.domain.dto.plan.PlanTrain

data class CoachingRequest(
    val coachTone: String,
    val totalDistance: Float,
    val nowDistance: Float,
    val meanHeartRate: Int,
    val meanPace: Int,
    val meanCadence: Int,
    val planTrain: PlanTrain
)
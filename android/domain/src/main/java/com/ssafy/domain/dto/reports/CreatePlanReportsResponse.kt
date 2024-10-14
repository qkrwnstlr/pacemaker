package com.ssafy.domain.dto.reports

import com.ssafy.domain.dto.plan.PlanTrain

data class CreatePlanReportsResponse(
    val planTrain: PlanTrain,
    val trainReport: TrainReport
)
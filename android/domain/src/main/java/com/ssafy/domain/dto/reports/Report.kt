package com.ssafy.domain.dto.reports

import com.ssafy.domain.dto.plan.PlanTrain

data class Report(
    val planTrain: PlanTrain? = null,
    val trainReport: TrainReport? = null
)

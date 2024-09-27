package com.ssafy.domain.dto.reports

data class Report(
    val planTrain: com.ssafy.domain.dto.plan.PlanTrain? = null,
    val trainReport: TrainReport? = null
)

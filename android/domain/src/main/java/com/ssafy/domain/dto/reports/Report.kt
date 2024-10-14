package com.ssafy.domain.dto.reports

import com.ssafy.domain.dto.plan.PlanTrain
import java.time.LocalDate

data class Report(
    val planTrain: PlanTrain? = null,
    val trainReport: TrainReport? = null,
    @Transient val trainingState: Int = 3,
    @Transient val date: LocalDate = LocalDate.now()
)

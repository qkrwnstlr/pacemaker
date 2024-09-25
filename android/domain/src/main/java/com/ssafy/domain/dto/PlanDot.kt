package com.ssafy.domain.dto

import java.time.LocalDate

data class PlanDot(
    val trainDateList: List<LocalDate>,
    val dailyTrainDateList: List<LocalDate>
)

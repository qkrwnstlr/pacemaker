package com.ssafy.domain.dto.plan

data class PlanRequest(
    val uid: String = "",
    val context: Context = Context(),
    val plan: Plan = Plan()
)

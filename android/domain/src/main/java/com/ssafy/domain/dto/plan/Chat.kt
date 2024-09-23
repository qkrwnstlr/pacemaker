package com.ssafy.domain.dto.plan

data class Chat(
    val message: String = "",
    val context: Context = Context(),
    val plan: Plan = Plan()
)

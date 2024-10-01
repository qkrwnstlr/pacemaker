package com.ssafy.domain.dto.plan

data class Chat(
    val message: String = "",
    val context: Context? = null,
    val plan: Plan = Plan(),
    val coachTone: String = "",
    @Transient val isModify: Boolean = false
)

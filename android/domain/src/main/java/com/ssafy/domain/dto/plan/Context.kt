package com.ssafy.domain.dto.plan

data class Context(
    val goal: String = "",
    val goalTime: Int = 0,
    val goalDistance: Int = 0,
    val trainDayOfWeek: List<String> = emptyList(),
    val userInfo: UserInfo = UserInfo()
)

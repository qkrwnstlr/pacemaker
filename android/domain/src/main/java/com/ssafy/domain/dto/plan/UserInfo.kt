package com.ssafy.domain.dto.plan

data class UserInfo(
    val age: String = "",
    val height: Int = 0,
    val weight: Int = 0,
    val gender: String = "",
    val injuries: List<String> = emptyList(),
    val recentRunPace: Int = 0,
    val recentRunDistance: Int = 0,
    val recentRunHeartRate: Int = 0
)

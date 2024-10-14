package com.ssafy.domain.dto

data class User(
    val uid: String = "",
    val name: String = "",
    val cadence: Int = 0,
    val distance: Int = 0,
    val height: Int = 0,
    val minute: Int = 0,
    val pace: Int = 0,
    val weight: Int = 0,
    val age: Int = 0,
    val trainCount: Int = 0,
    val trainTime: Int = 0,
    val trainDistance: Float = 0f,
    val gender: String = "UNKNOWN",
    val coachNumber: Long = 0,
    val injuries: List<String> = arrayListOf()
)
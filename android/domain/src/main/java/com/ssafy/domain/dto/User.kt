package com.ssafy.domain.dto

data class User(
    val uid: String,
    val name: String = "",
    val cadence: Int = 0,
    val distance: Int = 0,
    val gender: Int = 0,
    val height: Int = 0,
    val minute: Int = 0,
    val pace: Int = 0,
    val weight: Int = 0,
    val year: Int = 0
)
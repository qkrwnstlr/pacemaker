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
    val year: Int = 0,
    val trainCount: Int = 0,
    val trainTime: Int = 0,
    val trainDistance: Float = 0f,
    val gender: Int? = 1,//null, todo:백에서 null처리해주면 이것도 null로
    val coachNumber: Long? = null
)
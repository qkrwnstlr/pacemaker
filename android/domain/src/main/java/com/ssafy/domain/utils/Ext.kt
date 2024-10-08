package com.ssafy.domain.utils

import com.ssafy.domain.dto.User


const val MIKE_FEAT = "#엄격한 #열정 #할 수 있습니다"
const val JAMIE_FEAT = "#격려 #칭찬 #함께 해봐요"
const val DANNY_FEAT = "#유머러스 #경상도 사투리 #가보입시다"

const val MIKE = 1L
const val JAMIE = 2L
const val DANNY = 3L

const val MALE = "남성"
const val FEMALE = "여성"

fun Int.ifZero(defaultValue: () -> Int): Int = if (this == 0) defaultValue() else this

fun User.toMakeFeature(): String = when (coachNumber) {
    MIKE -> MIKE_FEAT
    JAMIE -> JAMIE_FEAT
    DANNY -> DANNY_FEAT
    else -> ""
}
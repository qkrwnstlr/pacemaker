package com.ssafy.domain.dto

data class LoginResponseBody(
    val userInfoResponse: User,
    val isAlreadyExists: Boolean
)
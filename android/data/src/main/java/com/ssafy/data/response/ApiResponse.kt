package com.ssafy.data.response

data class ApiResponse<T>(
    val data: T,
    val message: String?
)
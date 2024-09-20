package com.ssafy.domain.response

sealed class ResponseResult<T>(
    val data: T?,
    val message: String = ""
) {
    class Success<T>(data: T?, message: String) : ResponseResult<T>(data, message)
    class Error<T>(data: T? = null, message: String) : ResponseResult<T>(data, message)
}
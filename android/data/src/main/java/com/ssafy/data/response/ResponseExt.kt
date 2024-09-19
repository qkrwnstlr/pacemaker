package com.ssafy.data.response

import com.ssafy.domain.response.ResponseResult
import retrofit2.Response

const val NO_RESPONSE = "서버 응답 없음"
const val ERROR = "에러 발생"

inline fun <reified T> Response<ApiResponse<T>>.toResponseResult(): ResponseResult<T> {

    body()?.let {
        if (isSuccessful) return ResponseResult.Success(it.data, it.message ?: "")
        else return ResponseResult.Error(message = it.message ?: ERROR)
    }

    errorBody()?.let {
        val errorMessage = it.charStream().readLines().joinToString()
        if (errorMessage.isNotBlank()) return ResponseResult.Error(message = errorMessage)
    }

    return if (isSuccessful) ResponseResult.Success(null, "")
    else ResponseResult.Error(message = NO_RESPONSE)
}

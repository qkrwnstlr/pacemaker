package com.ssafy.data.source.user

import com.ssafy.data.response.ApiResponse
import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.User
import retrofit2.Response

interface UserDataSource {

    suspend fun signUp(user: User): Response<ApiResponse<Unit>>

    suspend fun modify(user: User): Response<ApiResponse<User>>

    suspend fun delete(user: User): Response<ApiResponse<Unit>>

    suspend fun getInfo(uid: String): Response<ApiResponse<User>>

    suspend fun getCoach(uid: String): Response<ApiResponse<Coach>>

    suspend fun setCoach(uid: String, coach: Coach): Response<ApiResponse<Unit>>

}
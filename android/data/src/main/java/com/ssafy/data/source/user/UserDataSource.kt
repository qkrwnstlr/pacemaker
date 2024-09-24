package com.ssafy.data.source.user

import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.LoginRequestBody
import com.ssafy.domain.dto.LoginResponseBody
import com.ssafy.domain.dto.User
import retrofit2.Response

interface UserDataSource {

    suspend fun signUp(uid: String, name: LoginRequestBody): Response<LoginResponseBody>

    suspend fun modify(uid: String, user: User): Response<User>

    suspend fun delete(uid: String): Response<Unit>

    suspend fun getInfo(uid: String): Response<User>

    suspend fun getCoach(uid: String): Response<Coach>

    suspend fun setCoach(uid: String, coach: Coach): Response<Unit>

}
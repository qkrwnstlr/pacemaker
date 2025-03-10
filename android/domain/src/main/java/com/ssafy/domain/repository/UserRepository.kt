package com.ssafy.domain.repository

import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.LoginRequestBody
import com.ssafy.domain.dto.LoginResponseBody
import com.ssafy.domain.dto.User
import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.response.ResponseResult

interface UserRepository {

    suspend fun signUp(uid: String, name: LoginRequestBody): ResponseResult<LoginResponseBody>

    suspend fun modify(uid: String, user: User): ResponseResult<User>

    suspend fun delete(uid: String): ResponseResult<Unit>

    suspend fun getInfo(uid: String): ResponseResult<User>

    suspend fun getCoach(uid: String): ResponseResult<Coach>

    suspend fun setCoach(uid: String, coach: Coach): ResponseResult<Unit>

    suspend fun getCalendarDot(
        uid: String,
        year: Int,
        month: Int
    ): ResponseResult<Map<String, List<ContentListDto>>>
}

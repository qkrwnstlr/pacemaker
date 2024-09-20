package com.ssafy.domain.repository

import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.User
import com.ssafy.domain.response.ResponseResult

interface UserRepository {

    suspend fun signUp(user: User): ResponseResult<Unit>

    suspend fun modify(user: User): ResponseResult<User>

    suspend fun delete(user: User): ResponseResult<Unit>

    suspend fun getInfo(uid: String): ResponseResult<User>

    suspend fun getCoach(uid: String): ResponseResult<Coach>

    suspend fun setCoach(uid: String, coach: Coach): ResponseResult<Unit>

}

package com.ssafy.data.source.user

import com.ssafy.domain.dto.CheckUid
import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.Exist
import com.ssafy.domain.dto.User
import retrofit2.Response

interface UserDataSource {

    suspend fun signUp(user: User): Response<Unit>

    suspend fun checkUid(checkUid: CheckUid): Response<Exist>

    suspend fun modify(user: User): Response<User>

    suspend fun delete(user: User): Response<Unit>

    suspend fun getInfo(uid: String): Response<User>

    suspend fun getCoach(uid: String): Response<Coach>

    suspend fun setCoach(uid: String, coach: Coach): Response<Unit>

}
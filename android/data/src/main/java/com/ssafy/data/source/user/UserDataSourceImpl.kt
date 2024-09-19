package com.ssafy.data.source.user

import com.ssafy.data.api.UserAPI
import com.ssafy.data.response.ApiResponse
import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.User
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun signUp(user: User): Response<ApiResponse<Unit>> =
        userAPI.signUp(user)

    override suspend fun modify(user: User): Response<ApiResponse<User>> =
        userAPI.modify(user)

    override suspend fun delete(user: User): Response<ApiResponse<Unit>> =
        userAPI.delete(user)

    override suspend fun getInfo(uid: String): Response<ApiResponse<User>> =
        userAPI.getInfo(uid)

    override suspend fun getCoach(uid: String): Response<ApiResponse<Coach>> =
        userAPI.getCoach(uid)

    override suspend fun setCoach(uid: String, coach: Coach): Response<ApiResponse<Unit>> =
        userAPI.setCoach(uid, coach)

}

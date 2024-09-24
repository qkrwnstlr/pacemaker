package com.ssafy.data.source.user

import com.ssafy.data.api.UserAPI
import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.LoginRequestBody
import com.ssafy.domain.dto.LoginResponseBody
import com.ssafy.domain.dto.User
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun signUp(uid: String, name: LoginRequestBody): Response<LoginResponseBody> =
        userAPI.signUp(uid, name)

    override suspend fun modify(uid: String, user: User): Response<User> =
        userAPI.modify(uid, user)

    override suspend fun delete(uid: String): Response<Unit> =
        userAPI.delete(uid)

    override suspend fun getInfo(uid: String): Response<User> =
        userAPI.getInfo(uid)

    override suspend fun getCoach(uid: String): Response<Coach> =
        userAPI.getCoach(uid)

    override suspend fun setCoach(uid: String, coach: Coach): Response<Unit> =
        userAPI.setCoach(uid, coach)

}

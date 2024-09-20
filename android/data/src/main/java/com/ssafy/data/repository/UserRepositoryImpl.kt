package com.ssafy.data.repository

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toResponseResult
import com.ssafy.data.source.user.UserDataSource
import com.ssafy.domain.dto.Coach
import com.ssafy.domain.dto.User
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.response.ResponseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun signUp(user: User): ResponseResult<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.signUp(user) }
        return response.toResponseResult()
    }

    override suspend fun modify(user: User): ResponseResult<User> {
        val response = withContext(ioDispatcher) { userDataSource.modify(user) }
        return response.toResponseResult()
    }

    override suspend fun delete(user: User): ResponseResult<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.delete(user) }
        return response.toResponseResult()
    }

    override suspend fun getInfo(uid: String): ResponseResult<User> {
        val response = withContext(ioDispatcher) { userDataSource.getInfo(uid) }
        return response.toResponseResult()
    }

    override suspend fun getCoach(uid: String): ResponseResult<Coach> {
        val response = withContext(ioDispatcher) { userDataSource.getCoach(uid) }
        return response.toResponseResult()
    }

    override suspend fun setCoach(uid: String, coach: Coach): ResponseResult<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.setCoach(uid, coach) }
        return response.toResponseResult()
    }

}

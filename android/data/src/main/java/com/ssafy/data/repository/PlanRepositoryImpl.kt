package com.ssafy.data.repository

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toResponseResult
import com.ssafy.data.source.plan.PlanDataSource
import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepositoryImpl @Inject constructor(
    private val planDataSource: PlanDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PlanRepository {

    override suspend fun chatForPlan(chat: Chat): ResponseResult<Chat> {
        val response = withContext(ioDispatcher) { planDataSource.chatForPlan(chat) }
        return response.toResponseResult()
    }

    override suspend fun makePlan(planRequest: PlanRequest): ResponseResult<Unit> {
        val response = withContext(ioDispatcher) { planDataSource.makePlan(planRequest) }
        return response.toResponseResult()
    }

    override suspend fun getPlan(uid: String): ResponseResult<PlanInfo> {
        val response = withContext(ioDispatcher) { planDataSource.getPlan(uid) }
        return response.toResponseResult()
    }

}

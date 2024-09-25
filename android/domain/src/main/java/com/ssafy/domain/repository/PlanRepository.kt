package com.ssafy.domain.repository

import com.ssafy.domain.dto.PlanDot
import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.response.ResponseResult

interface PlanRepository {

    suspend fun chatForPlan(chat: Chat): ResponseResult<Chat>
    suspend fun getTrainDot(uid: String, year: Int, month: Int): ResponseResult<PlanDot>

    suspend fun makePlan(planRequest: PlanRequest): ResponseResult<Unit>

    suspend fun getPlan(uid: String): ResponseResult<PlanInfo>

    suspend fun deletePlan(uid: String): ResponseResult<Unit>

}

package com.ssafy.data.source.plan

import com.ssafy.domain.dto.PlanDot
import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import retrofit2.Response

interface PlanDataSource {

    suspend fun chatForPlan(chat: Chat): Response<Chat>
    suspend fun getPlanDot(uid: String, year: Int, month: Int): Response<PlanDot>

    suspend fun makePlan(planRequest: PlanRequest): Response<Unit>

    suspend fun getPlan(uid: String): Response<PlanInfo>

}

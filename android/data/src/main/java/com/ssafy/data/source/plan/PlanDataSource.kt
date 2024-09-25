package com.ssafy.data.source.plan

import com.ssafy.domain.dto.PlanDot
import com.ssafy.domain.dto.plan.Chat
import retrofit2.Response

interface PlanDataSource {

    suspend fun chatForPlan(chat: Chat): Response<Chat>
    suspend fun getPlanDot(uid: String, year: Int, month: Int): Response<PlanDot>

}

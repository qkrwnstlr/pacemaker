package com.ssafy.data.source.plan

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.dto.schedule.ProgressData
import retrofit2.Response

interface PlanDataSource {

    suspend fun chatForPlan(chat: Chat): Response<Chat>
    suspend fun chatForModifyPlan(chat: Chat): Response<Chat>
    suspend fun getProgress(uid: String, year: Int, month: Int, day: Int): Response<ProgressData>
    suspend fun makePlan(planRequest: PlanRequest): Response<Unit>
    suspend fun getPlan(uid: String): Response<PlanInfo>
    suspend fun deletePlan(uid: String): Response<Unit>

}

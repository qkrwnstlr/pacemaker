package com.ssafy.data.source.plan

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.dto.schedule.ProgressData
import retrofit2.Response
import java.time.LocalDate

interface PlanDataSource {

    suspend fun chatForPlan(chat: Chat): Response<Chat>
    suspend fun getProgress(date: LocalDate): Response<ProgressData>

    suspend fun makePlan(planRequest: PlanRequest): Response<Unit>
    suspend fun getPlan(uid: String): Response<PlanInfo>
    suspend fun deletePlan(uid: String): Response<Unit>

}

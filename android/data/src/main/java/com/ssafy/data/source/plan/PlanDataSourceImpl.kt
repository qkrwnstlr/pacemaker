package com.ssafy.data.source.plan

import com.ssafy.data.api.PlanAPI
import com.ssafy.domain.dto.PlanDot
import com.ssafy.domain.dto.plan.Chat
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanDataSourceImpl @Inject constructor(private val planAPI: PlanAPI) : PlanDataSource {

    override suspend fun chatForPlan(chat: Chat): Response<Chat> =
        planAPI.chatForPlan(chat)

    override suspend fun getPlanDot(uid: String, year: Int, month: Int): Response<PlanDot> =
        planAPI.getPlanDot(uid, year, month)

}

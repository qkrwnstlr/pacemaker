package com.ssafy.data.source.plan

import com.ssafy.data.api.PlanAPI
import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.dto.schedule.ProgressData
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanDataSourceImpl @Inject constructor(private val planAPI: PlanAPI) : PlanDataSource {

    override suspend fun chatForPlan(chat: Chat): Response<Chat> =
        planAPI.chatForPlan(chat)

    override suspend fun makePlan(planRequest: PlanRequest): Response<Unit> =
        planAPI.makePlan(planRequest)

    override suspend fun getPlan(uid: String): Response<PlanInfo> =
        planAPI.getPlan(uid)

    override suspend fun deletePlan(uid: String): Response<Unit> =
        planAPI.deletePlan(uid)


    override suspend fun getProgress(uid:String, year:Int, month:Int, day:Int): Response<ProgressData> =
        planAPI.getProgress(uid, year, month, day)
}

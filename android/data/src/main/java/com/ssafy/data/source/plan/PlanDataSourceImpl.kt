package com.ssafy.data.source.plan

import com.ssafy.data.api.PlanAPI
import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.schedule.DayContentData
import com.ssafy.domain.dto.schedule.ProgressData
import retrofit2.Response
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanDataSourceImpl @Inject constructor(private val planAPI: PlanAPI) : PlanDataSource {

    override suspend fun chatForPlan(chat: Chat): Response<Chat> =
        planAPI.chatForPlan(chat)

    override suspend fun getPlanDot(
        uid: String,
        year: Int,
        month: Int
    ): Response<List<DayContentData>> =
        planAPI.getPlanDot(uid, year, month)

    override suspend fun getProgress(date: LocalDate): Response<ProgressData> =
        planAPI.getProgress(date)
}

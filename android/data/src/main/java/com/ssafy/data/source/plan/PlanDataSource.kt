package com.ssafy.data.source.plan

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.schedule.DayContentData
import com.ssafy.domain.dto.schedule.ProgressData
import retrofit2.Response
import java.time.LocalDate

interface PlanDataSource {

    suspend fun chatForPlan(chat: Chat): Response<Chat>
    suspend fun getPlanDot(uid: String, year: Int, month: Int): Response<List<DayContentData>>
    suspend fun getProgress(date: LocalDate): Response<ProgressData>
}

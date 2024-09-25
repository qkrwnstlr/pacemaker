package com.ssafy.domain.repository

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.schedule.DayContentData
import com.ssafy.domain.dto.schedule.ProgressData
import com.ssafy.domain.response.ResponseResult
import java.time.LocalDate

interface PlanRepository {

    suspend fun chatForPlan(chat: Chat): ResponseResult<Chat>
    suspend fun getTrainDot(
        uid: String,
        year: Int,
        month: Int
    ): ResponseResult<List<DayContentData>>

    suspend fun getProgress(date: LocalDate): ResponseResult<ProgressData>
}

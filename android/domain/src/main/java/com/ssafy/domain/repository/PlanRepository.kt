package com.ssafy.domain.repository

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.response.ResponseResult

interface PlanRepository {

    suspend fun chatForPlan(chat: Chat): ResponseResult<Chat>

}

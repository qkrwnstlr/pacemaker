package com.ssafy.data.api

import com.ssafy.domain.dto.plan.Chat
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PlanAPI {

    @POST("plans/chat")
    suspend fun chatForPlan(@Body chat: Chat): Response<Chat>

}

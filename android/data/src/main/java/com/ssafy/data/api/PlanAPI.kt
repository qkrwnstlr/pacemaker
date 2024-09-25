package com.ssafy.data.api

import com.ssafy.domain.dto.PlanDot
import com.ssafy.domain.dto.plan.Chat
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlanAPI {

    @POST("plans/chat")
    suspend fun chatForPlan(@Body chat: Chat): Response<Chat>

    @GET("plans/{uid}")
    suspend fun getPlanDot(
        @Path("uid") uid: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<PlanDot>
}

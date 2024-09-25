package com.ssafy.data.api

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlanAPI {

    @POST("plans/chat")
    suspend fun chatForPlan(@Body chat: Chat): Response<Chat>

    @POST("plans/create")
    suspend fun makePlan(@Body planRequest: PlanRequest): Response<Unit>

    @GET("plans/active/user/{uid}")
    suspend fun getPlan(@Path("uid") uid: String): Response<PlanInfo>

}

package com.ssafy.data.api

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.dto.schedule.ProgressData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDate

interface PlanAPI {

    @POST("plans/chat")
    suspend fun chatForPlan(@Body chat: Chat): Response<Chat>

    @POST("plans/create")
    suspend fun makePlan(@Body planRequest: PlanRequest): Response<Unit>

    @GET("plans/active/user/{uid}")
    suspend fun getPlan(@Path("uid") uid: String): Response<PlanInfo>

    @DELETE("plans/active/user/{uid}")
    suspend fun deletePlan(@Path("uid") uid: String): Response<Unit>

    @GET("plans/status/{date}")
    suspend fun getProgress(@Path("date") date: LocalDate): Response<ProgressData>
}


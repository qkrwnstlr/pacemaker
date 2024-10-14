package com.ssafy.data.api

import com.ssafy.domain.dto.plan.Chat
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanRequest
import com.ssafy.domain.dto.schedule.ProgressData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlanAPI {

    @POST("plans/chat")
    suspend fun chatForPlan(@Body chat: Chat): Response<Chat>

    @POST("plans/update/chat")
    suspend fun chatForModifyPlan(@Body chat: Chat): Response<Chat>

    @GET("plans/active/user/{uid}")
    suspend fun getPlan(@Path("uid") uid: String): Response<PlanInfo>

    @POST("plans/create")
    suspend fun makePlan(@Body planRequest: PlanRequest): Response<Unit>

    @PATCH("plans")
    suspend fun modifyPlan(@Body planRequest: PlanRequest): Response<Unit>

    @DELETE("plans/active/user/{uid}")
    suspend fun deletePlan(@Path("uid") uid: String): Response<Unit>

    @GET("plans/progress/user/{uid}")
    suspend fun getProgress(
        @Path("uid") uid: String,
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int
    ): Response<ProgressData>
}


package com.ssafy.data.api

import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import com.ssafy.domain.dto.reports.Report
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportsAPI {
    @POST("reports/plan")
    suspend fun createPlanReports(@Body createPlanReportsRequest: CreatePlanReportsRequest): Response<CreatePlanReportsResponse>

    @GET("reports/{id}/daily/user/{uid}")
    suspend fun getDailyReport(@Path("id") id: Int, @Path("uid") uid: String): Response<Report>

    @GET("reports/{id}/free/user/{uid}")
    suspend fun getFreeReport(@Path("id") id: Int, @Path("uid") uid: String): Response<Report>

    @GET("reports/{id}/plan/user/{uid}")
    suspend fun getPlanReport(@Path("id") id: Int, @Path("uid") uid: String): Response<Report>

    @GET("plans/train/before/{id}/user/{uid}")
    suspend fun getBeforePlanReport(@Path("id") id: Int, @Path("uid") uid: String): Response<Report>
}
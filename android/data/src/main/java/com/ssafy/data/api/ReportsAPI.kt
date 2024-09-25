package com.ssafy.data.api

import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportsAPI {
    @POST("report/plan")
    suspend fun createPlanReports(@Body createPlanReportsRequest: CreatePlanReportsRequest): Response<CreatePlanReportsResponse>
}
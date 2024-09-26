package com.ssafy.data.source.reports

import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import retrofit2.Response

interface ReportsDataSource {
    suspend fun createPlanReports(createPlanReportsRequest: CreatePlanReportsRequest) : Response<CreatePlanReportsResponse>
}
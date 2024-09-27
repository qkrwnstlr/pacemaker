package com.ssafy.data.source.reports

import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import com.ssafy.domain.dto.reports.Report
import retrofit2.Response

interface ReportsDataSource {
    suspend fun createPlanReports(createPlanReportsRequest: CreatePlanReportsRequest): Response<CreatePlanReportsResponse>

    suspend fun getDailyReport(id: Int, uid: String): Response<Report>
    suspend fun getFreeReport(id: Int, uid: String): Response<Report>
    suspend fun getPlanReport(id: Int, uid: String): Response<Report>
    suspend fun getBeforePlanReport(id: Int, uid: String): Response<Report>
}
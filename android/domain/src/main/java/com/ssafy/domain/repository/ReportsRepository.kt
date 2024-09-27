package com.ssafy.domain.repository

import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.response.ResponseResult

interface ReportsRepository {
    suspend fun createPlanReports(createPlanReportsRequest: CreatePlanReportsRequest): ResponseResult<CreatePlanReportsResponse>

    suspend fun getDailyReport(id: Int, uid: String): ResponseResult<Report>
    suspend fun getFreeReport(id: Int, uid: String): ResponseResult<Report>
    suspend fun getPlanReport(id: Int, uid: String): ResponseResult<Report>
    suspend fun getBeforePlanReport(id: Int, uid: String): ResponseResult<Report>
}
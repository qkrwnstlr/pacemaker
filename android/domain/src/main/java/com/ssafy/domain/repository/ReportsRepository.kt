package com.ssafy.domain.repository

import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import com.ssafy.domain.response.ResponseResult

interface ReportsRepository {
    suspend fun createPlanReports(createPlanReportsRequest: CreatePlanReportsRequest) : ResponseResult<CreatePlanReportsResponse>
}
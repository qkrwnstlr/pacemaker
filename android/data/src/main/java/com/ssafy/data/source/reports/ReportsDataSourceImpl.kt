package com.ssafy.data.source.reports

import com.ssafy.data.api.ReportsAPI
import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import retrofit2.Response
import javax.inject.Inject

class ReportsDataSourceImpl @Inject constructor(private val reportsAPI: ReportsAPI) :
    ReportsDataSource {
    override suspend fun createPlanReports(createPlanReportsRequest: CreatePlanReportsRequest): Response<CreatePlanReportsResponse> =
        reportsAPI.createPlanReports(createPlanReportsRequest)
}
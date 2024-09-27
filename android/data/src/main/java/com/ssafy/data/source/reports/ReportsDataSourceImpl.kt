package com.ssafy.data.source.reports

import com.ssafy.data.api.ReportsAPI
import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import com.ssafy.domain.dto.reports.Report
import retrofit2.Response
import javax.inject.Inject

class ReportsDataSourceImpl @Inject constructor(private val reportsAPI: ReportsAPI) :
    ReportsDataSource {
    override suspend fun createPlanReports(createPlanReportsRequest: CreatePlanReportsRequest): Response<CreatePlanReportsResponse> =
        reportsAPI.createPlanReports(createPlanReportsRequest)

    override suspend fun getDailyReport(id: Int, uid: String): Response<Report> =
        reportsAPI.getDailyReport(id, uid)

    override suspend fun getFreeReport(id: Int, uid: String): Response<Report> =
        reportsAPI.getFreeReport(id, uid)

    override suspend fun getPlanReport(id: Int, uid: String): Response<Report> =
        reportsAPI.getPlanReport(id, uid)

    override suspend fun getBeforePlanReport(id: Int, uid: String): Response<Report> =
        reportsAPI.getBeforePlanReport(id, uid)
}
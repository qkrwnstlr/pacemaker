package com.ssafy.data.repository

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toResponseResult
import com.ssafy.data.source.reports.ReportsDataSource
import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.repository.ReportsRepository
import com.ssafy.domain.response.ResponseResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
    private val reportsDataSource: ReportsDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ReportsRepository {
    override suspend fun createPlanReports(createPlanReportsRequest: CreatePlanReportsRequest): ResponseResult<CreatePlanReportsResponse> {
        val response =
            withContext(ioDispatcher) { reportsDataSource.createPlanReports(createPlanReportsRequest) }
        return response.toResponseResult()
    }

    override suspend fun getDailyReport(id: Int, uid: String): ResponseResult<Report> {
        val response = withContext(ioDispatcher) { reportsDataSource.getDailyReport(id, uid) }
        return response.toResponseResult()
    }

    override suspend fun getFreeReport(id: Int, uid: String): ResponseResult<Report> {
        val response = withContext(ioDispatcher) { reportsDataSource.getFreeReport(id, uid) }
        return response.toResponseResult()
    }

    override suspend fun getPlanReport(id: Int, uid: String): ResponseResult<Report> {
        val response = withContext(ioDispatcher) { reportsDataSource.getPlanReport(id, uid) }
        return response.toResponseResult()
    }

    override suspend fun getBeforePlanReport(id: Int, uid: String): ResponseResult<Report> {
        val response = withContext(ioDispatcher) { reportsDataSource.getBeforePlanReport(id, uid) }
        return response.toResponseResult()
    }
}
package com.ssafy.domain.usecase.reports

import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import com.ssafy.domain.repository.ReportsRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class CreatePlanReportsUseCase @Inject constructor(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(createPlanReportsRequest: CreatePlanReportsRequest): ResponseResult<CreatePlanReportsResponse> {
        val result = reportsRepository.createPlanReports(createPlanReportsRequest)
        if (result is ResponseResult.Error) throw RuntimeException(result.message)
        return result
    }
}
package com.ssafy.domain.usecase.reports

import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.repository.ReportsRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetReportUseCase @Inject constructor(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(content: ContentListDto, uid: String): Report {
        val result =
            when (content.type) {
                "DAILY_REPORT" -> {
                    reportsRepository.getDailyReport(content.id, uid)
                }

                "FREE_REPORT" -> {
                    reportsRepository.getFreeReport(content.id, uid)
                }

                "PLAN_REPORT" -> {
                    reportsRepository.getPlanReport(content.id, uid)
                }

                else -> {
                    reportsRepository.getBeforePlanReport(content.id, uid)
                }
            }
        if (result is ResponseResult.Error) throw RuntimeException(result.message)
        return result.data ?: throw RuntimeException()
    }
}
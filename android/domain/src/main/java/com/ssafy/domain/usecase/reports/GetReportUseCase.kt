package com.ssafy.domain.usecase.reports

import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.ReportsRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetReportUseCase @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(content: ContentListDto): Report {
        val uid = dataStoreRepository.getUser().uid
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
package com.ssafy.domain.usecase.reports

import com.ssafy.domain.dto.reports.CreatePlanReportsRequest
import com.ssafy.domain.dto.reports.CreatePlanReportsResponse
import com.ssafy.domain.dto.reports.TrainResult
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.ReportsRepository
import com.ssafy.domain.response.ResponseResult
import java.time.LocalDateTime
import javax.inject.Inject

class CreatePlanReportsUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(
        planTrainId: Long,
        trainResult: TrainResult
    ): CreatePlanReportsResponse {
        val user = dataStoreRepository.getUser()
        val request = CreatePlanReportsRequest(
            user.uid,
            planTrainId,
            user.coachNumber,
            LocalDateTime.now().toString(),
            trainResult,
        )
        val result = reportsRepository.createPlanReports(request)
        return result.data ?: throw RuntimeException(result.message)
    }
}
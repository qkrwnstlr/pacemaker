package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.schedule.ProgressData
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import java.time.LocalDate
import javax.inject.Inject

class GetProgressUseCase @Inject constructor(
    private val planRepository: PlanRepository
) {
    suspend operator fun invoke(uid: String, date: LocalDate): ProgressData {
        val response = planRepository.getProgress(uid, date.year, date.monthValue, date.dayOfMonth)

        if (response is ResponseResult.Error) throw RuntimeException(response.message)
        return response.data ?: throw RuntimeException()
    }
}
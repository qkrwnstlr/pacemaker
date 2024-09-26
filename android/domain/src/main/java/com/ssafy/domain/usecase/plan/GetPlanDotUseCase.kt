package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.schedule.DayContentData
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetPlanDotUseCase @Inject constructor(private val planRepository: PlanRepository) {
    suspend operator fun invoke(
        uid: String,
        year: Int,
        month: Int
    ): ResponseResult<List<DayContentData>> = planRepository.getTrainDot(uid, year, month)
}
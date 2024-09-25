package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetPlanInfoUseCase @Inject constructor(
    private val planRepository: PlanRepository
) {

    suspend operator fun invoke(uid: String): PlanInfo {
        val response = planRepository.getPlan(uid)
        if (response is ResponseResult.Error) throw RuntimeException()

        response.data?.let { planInfo -> return planInfo } ?: throw RuntimeException()
    }

}
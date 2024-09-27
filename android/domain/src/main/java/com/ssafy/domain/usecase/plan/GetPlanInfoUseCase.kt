package com.ssafy.domain.usecase.plan

import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetPlanInfoUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val planRepository: PlanRepository
) {

    suspend operator fun invoke(): PlanInfo {
        val uid = dataStoreRepository.getUser().uid
        val response = planRepository.getPlan(uid)
        if (response is ResponseResult.Error) throw RuntimeException(response.message)

        response.data?.let { planInfo -> return planInfo } ?: throw RuntimeException()
    }

}
package com.ssafy.domain.usecase.plan

import com.ssafy.domain.repository.PlanRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class DeletePlanUseCase @Inject constructor(private val planRepository: PlanRepository) {

    suspend operator fun invoke(uid: String) {
        val response = planRepository.deletePlan(uid)
        if (response is ResponseResult.Error) throw RuntimeException(response.message)
    }

}

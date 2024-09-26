package com.ssafy.domain.usecase.train

import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.CoachingResponse
import com.ssafy.domain.repository.TrainRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetCoachingUseCase @Inject constructor(private val coachingRepository: TrainRepository) {

    suspend operator fun invoke(coachingRequest: CoachingRequest): ResponseResult<CoachingResponse> {
        return coachingRepository.getCoaching(coachingRequest)
    }
}

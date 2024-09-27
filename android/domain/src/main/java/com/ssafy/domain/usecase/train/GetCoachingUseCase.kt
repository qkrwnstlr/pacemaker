package com.ssafy.domain.usecase.train

import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.TrainRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetCoachingUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val coachingRepository: TrainRepository
) {

    suspend operator fun invoke(coachingRequest: CoachingRequest): String {
        val coachIndex = dataStoreRepository.getUser().coachNumber
        val newRequest = coachingRequest.copy(coachIndex = coachIndex)
        val response = coachingRepository.getCoaching(newRequest)
        if (response is ResponseResult.Error) throw RuntimeException()

        return response.data?.path ?: throw RuntimeException()
    }
}

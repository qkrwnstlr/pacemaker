package com.ssafy.domain.usecase.train

import com.ssafy.domain.dto.train.TTSRequest
import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.TrainRepository
import com.ssafy.domain.response.ResponseResult
import javax.inject.Inject

class GetTTSUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val coachingRepository: TrainRepository
) {

    suspend operator fun invoke(message: String, coachIndex: Long? = null): String {
        val coachNumber = coachIndex ?: dataStoreRepository.getUser().coachNumber
        val newRequest = TTSRequest(message, coachNumber)
        val response = coachingRepository.getTTS(newRequest)
        if (response is ResponseResult.Error) throw RuntimeException()

        return response.data?.path ?: throw RuntimeException()
    }
}

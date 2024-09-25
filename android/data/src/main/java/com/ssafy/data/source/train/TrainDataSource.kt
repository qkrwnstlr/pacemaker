package com.ssafy.data.source.train

import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.CoachingResponse
import retrofit2.Response

interface TrainDataSource {
    suspend fun getCoaching(dto: CoachingRequest): Response<CoachingResponse>
}
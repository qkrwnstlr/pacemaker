package com.ssafy.data.source.train

import com.ssafy.data.api.TrainAPI
import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.CoachingResponse
import retrofit2.Response
import javax.inject.Inject

class TrainDataSourceImpl @Inject constructor(private val trainAPI: TrainAPI) : TrainDataSource {
    override suspend fun getCoaching(dto: CoachingRequest): Response<CoachingResponse> =
        trainAPI.getCoaching(dto)
}
package com.ssafy.data.source.train

import com.ssafy.data.api.TrainAPI
import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.TTSRequest
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class TrainDataSourceImpl @Inject constructor(private val trainAPI: TrainAPI) : TrainDataSource {
    override suspend fun getCoaching(dto: CoachingRequest): Response<ResponseBody> =
        trainAPI.getCoaching(dto)

    override suspend fun getTTS(dto: TTSRequest): Response<ResponseBody> = trainAPI.getTTS(dto)
}
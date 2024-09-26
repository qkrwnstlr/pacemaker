package com.ssafy.data.source.train

import com.ssafy.domain.dto.train.CoachingRequest
import okhttp3.ResponseBody
import retrofit2.Response

interface TrainDataSource {
    suspend fun getCoaching(dto: CoachingRequest): Response<ResponseBody>
}
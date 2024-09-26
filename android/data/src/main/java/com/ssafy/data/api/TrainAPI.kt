package com.ssafy.data.api

import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.CoachingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TrainAPI {
    @POST("realtimes")
    suspend fun getCoaching(@Body dto: CoachingRequest): Response<CoachingResponse>
}
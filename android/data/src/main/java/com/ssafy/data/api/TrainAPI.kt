package com.ssafy.data.api

import com.ssafy.domain.dto.train.CoachingRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface TrainAPI {

    @POST("realtimes")
    @Streaming
    suspend fun getCoaching(@Body dto: CoachingRequest): Response<ResponseBody>

}

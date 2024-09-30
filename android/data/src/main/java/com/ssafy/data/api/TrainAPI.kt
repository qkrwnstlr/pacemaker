package com.ssafy.data.api

import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.dto.train.TTSRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface TrainAPI {

    @POST("realtimes/feedback")
    @Streaming
    suspend fun getCoaching(@Body dto: CoachingRequest): Response<ResponseBody>

    @POST("realtimes/tts")
    @Streaming
    suspend fun getTTS(@Body dto: TTSRequest): Response<ResponseBody>
}

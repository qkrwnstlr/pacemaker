package com.ssafy.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

interface TextToSpeechAPI {

    @GET("tts")
    @Streaming
    suspend fun getTTS(
        @Query("message") message: String,
        @Query("coach_index") coachIndex: Long
    ): Response<ResponseBody>

}

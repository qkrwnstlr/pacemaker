package com.ssafy.data.source.tts

import okhttp3.ResponseBody
import retrofit2.Response

interface TextToSpeechDataSource {

    suspend fun getTTS(message: String, coachIndex: Long): Response<ResponseBody>

}
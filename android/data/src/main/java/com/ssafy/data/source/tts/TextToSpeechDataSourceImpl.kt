package com.ssafy.data.source.tts

import com.ssafy.data.api.TextToSpeechAPI
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechDataSourceImpl @Inject constructor(
    private val textToSpeechAPI: TextToSpeechAPI
) : TextToSpeechDataSource {

    override suspend fun getTTS(message: String, coachIndex: Long): Response<ResponseBody> =
        textToSpeechAPI.getTTS(message, coachIndex)

}

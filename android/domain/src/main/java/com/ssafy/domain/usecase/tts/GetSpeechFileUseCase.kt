package com.ssafy.domain.usecase.tts

import com.ssafy.domain.repository.DataStoreRepository
import com.ssafy.domain.repository.TextToSpeechRepository
import com.ssafy.domain.response.ResponseResult
import java.io.File
import javax.inject.Inject

class GetSpeechFileUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val textToSpeechRepository: TextToSpeechRepository
) {

    suspend fun getSpeechFile(message: String): File {
        val coachIndex = dataStoreRepository.getUser().coachNumber
        val response = textToSpeechRepository.getTTS(message, coachIndex)
        if (response is ResponseResult.Error) throw RuntimeException()

        return response.data ?: throw RuntimeException()
    }

}

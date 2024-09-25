package com.ssafy.domain.repository

import com.ssafy.domain.response.ResponseResult
import java.io.File

interface TextToSpeechRepository {

    suspend fun getTTS(message: String, coachIndex: Long): ResponseResult<File>

}

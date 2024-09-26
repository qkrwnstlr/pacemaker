package com.ssafy.domain.repository

import com.ssafy.domain.dto.train.CoachingRequest
import com.ssafy.domain.response.ResponseResult
import java.io.File

interface TrainRepository {
    suspend fun getCoaching(coachingRequest: CoachingRequest): ResponseResult<File>
}
package com.ssafy.domain.dto.train

import com.google.gson.annotations.SerializedName

data class TTSRequest(
    val message: String,
    @SerializedName("coachNumber")
    val coachIndex: Long = 2L
)
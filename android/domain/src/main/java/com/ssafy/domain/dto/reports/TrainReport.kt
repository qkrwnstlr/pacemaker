package com.ssafy.domain.dto.reports

data class TrainReport(
    val trainDuration: List<String>, // time 타입을 String으로 가정
    val trainDistance: Int,
    val trainTime: Int,
    val heartRate: Int,
    val pace: Int,
    val cadence: Int,
    val kcal: Int,
    val heartZone: List<Int>,
    val splitData: List<SplitData>,
    val trainMap: List<Any>, // 자료형이 명확하지 않아 Any로 처리
    val trainEvaluation: TrainEvaluation,
    val coachMessage: List<String>
)
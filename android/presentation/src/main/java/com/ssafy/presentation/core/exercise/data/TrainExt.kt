package com.ssafy.presentation.core.exercise.data

import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.presentation.core.exercise.manager.TrainSession
import com.ssafy.presentation.core.exercise.manager.TrainState

val PlanTrain.message
    get() = when(paramType) {
        "time" -> "오늘은 ${trainParam.timeString} 동안 ${trainPace.timeString} 페이스로 ${repetition}회 달리고, " +  if(repetition == 1) "종료하겠습니다." else "중간에 ${interParam.timeString} 동안 걷겠습니다."
        "distance" -> "오늘은 ${trainParam.distanceString}를 ${trainPace.timeString} 페이스로 ${repetition}회 달리고, " + if(repetition == 1) "종료하겠습니다." else "중간에 ${interParam.distanceString} 동안 걷겠습니다."
        else -> ""
    }

val TrainState.message
    get() = when (this) {
        TrainState.None -> ""

        TrainState.Before -> "훈련이 시작되었습니다."

        is TrainState.WarmUp -> "Warm Up 세션 시작. ${session.message}"

        is TrainState.During -> when (session) {
            is TrainSession.Running -> "${step.toKorean()}번 세션 시작. ${session.message}"
            is TrainSession.Jogging -> "${step.toKorean()}번 세션 종료. ${session.message}"
        }

        is TrainState.CoolDown -> "Cool Down 세션 시작. ${session.message}"

        TrainState.Ended -> "훈련이 종료되었습니다."
        TrainState.Default -> "자유롭게 달려보세요"
    }

val TrainSession.message
    get() = when (this) {
        is TrainSession.Running -> when (type) {
            TrainSession.Type.TIME -> "${goal.timeString} 동안 ${pace.timeString} 페이스로 달리겠습니다."
            TrainSession.Type.DISTANCE -> "${goal.distanceString}를 ${pace.timeString} 페이스로 달리겠습니다."
        }

        is TrainSession.Jogging -> when (type) {
            TrainSession.Type.TIME -> "${goal.timeString} 동안 걷겠습니다."
            TrainSession.Type.DISTANCE -> "${goal.distanceString}를 걷겠습니다."
        }

    }

val Int.timeString
    get() = "${(this / 60).toKorean()}분${if(this % 60 != 0) " ${(this % 60).toKorean()}초" else ""}"

val Int.distanceString
    get() = "${this.toDouble() / 1000}km"

fun Int.toKorean(): String {
    val units = listOf("", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구")
    val tens = listOf("", "십", "백", "천")
    val largeUnits = listOf("", "만", "억", "조", "경")

    if (this == 0) return "영"

    var num = this
    var result = StringBuilder()
    var largeUnitIndex = 0

    while (num > 0) {
        val part = num % 10000
        if (part != 0) {
            var partStr = StringBuilder()
            var temp = part
            for (i in 0..3) {
                val digit = temp % 10
                if (digit != 0) {
                    val unitStr = if (i == 1 && digit == 1) tens[i] else units[digit] + tens[i]
                    partStr.insert(0, unitStr)
                }
                temp /= 10
            }
            result.insert(0, partStr.append(largeUnits[largeUnitIndex]))
        }
        num /= 10000
        largeUnitIndex++
    }

    return result.toString().trim()
}
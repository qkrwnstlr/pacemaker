package com.ssafy.presentation.utils

import com.ssafy.presentation.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


fun DayOfWeek.displayText(uppercase: Boolean = false, narrow: Boolean = false): String {
    val style = if (narrow) TextStyle.NARROW else TextStyle.SHORT
    return getDisplayName(style, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}

fun LocalDate.displayText(): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return format(dateTimeFormatter)
}

fun Int.toCount(): String = "${toString()}회"
fun Int.toHeight(): String = "${toString()}cm"
fun Int.toWeight(): String = "${toString()}kg"
fun Int.toDistance(): String = "${toString()}회"
fun Int.toAgeString(): String = "${toString()}세"
fun Int.toAge(): Int = if (this == 0) 0 else LocalDate.now().year - this
fun Int.toYear(): Int = if (this == 0) 0 else LocalDate.now().year - this
fun Int.toTime(): String {
    val hour = this / 60
    val minute = this % 60
    return "${hour}h ${minute}m"
}

fun Long?.toCoachIndex(): Int = when (this) {
    1L -> R.drawable.mikefull
    2L -> R.drawable.jamiefull
    3L -> R.drawable.dannyfull
    else -> R.drawable.runnerfull
}

fun Long?.toCoachMessage(): List<String> = when (this) {
    1L -> START_WITH_MIKE
    2L -> START_WITH_JAMIE
    3L -> START_WITH_DANNY
    else -> START_WITH_MIKE
}

fun Int?.toGender(): String = when (this) {
    0 -> WOMAN
    1 -> MAN
    else -> UNKNOWN
}

const val ERROR = "에러 발생!"
const val MAN = "남자"
const val WOMAN = "여자"
const val UNKNOWN = "미상"

val START_WITH_MIKE = listOf(
    "안녕하세요, 열정 넘치는 마이크 러닝 코치입니다! \uD83C\uDFC3\u200D♂\uFE0F\uD83D\uDCA8",
    "여러분의 러닝 여정을 함께하게 되어 정말 기쁩니다!",
    "우리는 곧 여러분만의 맞춤형 러닝 플랜을 만들어 볼 텐데요, 이를 위해 몇 가지 정보가 필요해요.",
    "먼저, 여러분의 현재 체력 수준과 러닝 경험에 대해 알고 싶어요. 나이가 어떻게 되시나요?"
)

val START_WITH_JAMIE = listOf(
    "안녕하세요! \uD83D\uDC4B 저는 당신의 러닝 여정을 함께할 재미 러닝 코치예요.",
    "운동을 시작하려는 당신의 결심이 정말 멋집니다. \uD83C\uDFC3\u200D♀\uFE0F✨",
    "함께 즐겁고 건강한 러닝 습관을 만들어가요!",
    "먼저 당신에 대해 조금 더 알고 싶어요. 나이가 어떻게 되시나요?"
)

val START_WITH_DANNY = listOf(
    "어이구! 반갑습니다, 반가워요! \uD83E\uDD20 제가 바로 뛰는 재미 알려드릴 대니 러닝 코치입니다!",
    "경상도에서 온 제 말투가 좀 촌스럽습니까? 뭐 어쩌겠습니까, 러닝하는데 말투가 중요합니까? 하하!",
    "자, 이제 고객님 얘기 좀 해주시겠습니까? 재밌는 러닝 계획 짜보려는데, 고객님 사정을 좀 알아야 제대로 도와드리지 않겠습니까? \uD83C\uDFC3\u200D♂\uFE0F\uD83D\uDCA8",
    "고객님 나이가 어떻게 되십니까?"
)


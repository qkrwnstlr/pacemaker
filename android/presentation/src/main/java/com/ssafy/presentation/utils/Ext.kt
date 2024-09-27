package com.ssafy.presentation.utils

import com.ssafy.domain.dto.User
import com.ssafy.domain.dto.plan.Plan
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.dto.plan.UserInfo
import com.ssafy.domain.utils.DANNY
import com.ssafy.domain.utils.JAMIE
import com.ssafy.domain.utils.MIKE
import com.ssafy.presentation.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt


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
fun Int.toTime(): String {
    val hour = this / 60
    val minute = this % 60
    return "${hour}h ${minute}m"
}

fun String.toGenderString(): String =
    if (this == "FEMALE") "여성" else if (this == "MALE") "남성" else "미상"

fun String.toAgeString(): String = if (isBlank()) "" else "${this}세"
fun Int.toEmptyOrHeight(): String = if (this == 0) "" else "${toString()}cm"
fun Int.toEmptyOrWeight(): String = if (this == 0) "" else "${toString()}kg"
fun List<String>.toInjuries(): String {
    val injuries = joinToString()
    return if (injuries.length > 15) "${injuries}..." else injuries
}

fun Double.toPaceString(): String {
    val pace = roundToInt()
    val minute = pace / 60
    val second = pace % 60
    return "${minute}'${second}\""
}

fun Int.toTimeString(): String {
    val minutes = this / 60
    val hour = minutes / 60
    val minute = minutes % 60
    return "${hour}h ${minute}m"
}

fun String.toMakeDurationDate(endDate: String): String = "$this ~ $endDate"

fun List<String>.toWeekString(): String {
    val builder = StringBuilder("매주 ")
    WEEK_LIST.forEach { day ->
        if (this.contains(day.first)) builder.append(day.second + " ")
    }
    return builder.toString()
}

fun Long?.toCoachIndex(): Int = when (this) {
    MIKE -> R.drawable.mikefull
    JAMIE -> R.drawable.jamiefull
    DANNY -> R.drawable.dannyfull
    else -> R.drawable.runnerfull
}

fun Long?.toCoachMessage(isModify: Boolean): List<String> = when (this) {
    MIKE -> if (isModify) MODIFY_WITH_MIKE else START_WITH_MIKE
    JAMIE -> if (isModify) MODIFY_WITH_JAMIE else START_WITH_JAMIE
    DANNY -> if (isModify) MODIFY_WITH_DANNY else START_WITH_DANNY
    else -> if (isModify) MODIFY_WITH_MIKE else START_WITH_MIKE
}

fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

fun Int.toTrainPace(): String {
    val minute = this / 60
    val second = this % 60
    return "${minute}'${second}\""
}

fun PlanTrain?.toPlanInst(): String {
    if (this == null) return ""

    val totalString = makeTotalString()
    val meanPace = trainPace.toTrainPace()
    return "$totalString  |  $meanPace"
}

fun PlanTrain?.makeTotalString(): String {
    if (this == null) return ""

    val totalDistance = sessionDistance / 1000f
    val distanceString = String.format(Locale.KOREAN, "%.2fkm", totalDistance)

    val totalTime = sessionTime / 60
    val timeString = "${totalTime}분"

    return if (totalDistance != 0f) distanceString else timeString
}

fun PlanTrain?.toTrainText(): String {
    if (this == null) return ""

    val totalString = makeTotalString()
    val runCount = "러닝 ${repetition}회"
    val interCount = if (repetition > 1) "천천히 걷기 ${repetition - 1}회" else ""
    return "${totalString}\n${runCount}\n${interCount}"
}

fun User.toUserInfo() = UserInfo(
    age = age.toString(),
    height = height,
    weight = weight,
    gender = gender,
    injuries = injuries,
    recentRunPace = 0,
    recentRunDistance = 0,
    recentRunHeartRate = 0
)

fun PlanInfo.toPlan(): Plan = Plan(
    totalDays = totalDays,
    totalTimes = totalTimes,
    totalDistances = totalDistances,
    planTrains = planTrains
)

const val ERROR = "에러 발생!"

val START_WITH_MIKE = listOf(
    "안녕하세요, 열정 넘치는 마이크 러닝 코치입니다! \uD83C\uDFC3\u200D♂\uFE0F\uD83D\uDCA8",
    "여러분의 러닝 여정을 함께하게 되어 정말 기쁩니다!",
    "우리는 곧 여러분만의 맞춤형 러닝 플랜을 만들어 볼 텐데요, 이를 위해 몇 가지 정보가 필요해요.",
    "먼저, 여러분의 현재 체력 수준과 러닝 경험에 대해 알고 싶어요. 목표가 어떻게 되시나요?"
)

val MODIFY_WITH_MIKE = listOf(
    "안녕하세요, 열정 넘치는 마이크 러닝 코치입니다! 플랜 수정을 위해 돌아왔군요! \uD83D\uDE4C\uD83C\uDFFB\uD83D\uDCAA",
    "러닝 플랜을 개선하고 싶으시다니 정말 대단해요! 지속적인 성장을 위한 첫 걸음이에요. \uD83D\uDCC8",
    "함께 여러분의 플랜을 업그레이드해 볼 텐데요, 이를 위해 몇 가지 궁금한 점이 있어요.",
    "현재 플랜에서 어떤 부분을 수정하고 싶으신가요? 특별히 힘든 부분이 있나요? 목표나 일정에 변화가 있나요? \uD83E\uDD14"
)

val START_WITH_JAMIE = listOf(
    "안녕하세요! \uD83D\uDC4B 저는 당신의 러닝 여정을 함께할 재미 러닝 코치예요.",
    "운동을 시작하려는 당신의 결심이 정말 멋집니다. \uD83C\uDFC3\u200D♀\uFE0F✨",
    "함께 즐겁고 건강한 러닝 습관을 만들어가요!",
    "먼저 당신에 대해 조금 더 알고 싶어요. 목표가 어떻게 되시나요?"
)

val MODIFY_WITH_JAMIE = listOf(
    "안녕하세요! \uD83D\uDE04 재미 러닝 코치 제이미예요. 플랜을 수정하러 오셨군요?",
    "와우! 플랜을 개선하려는 당신의 열정이 정말 대단해요. \uD83C\uDF1F",
    "함께 더 즐겁고 효과적인 러닝 계획을 만들어봐요. 이건 정말 신나는 일이 될 거예요! \uD83C\uDF89",
    "자, 어떤 부분을 바꿔보고 싶으세요? 힘들었던 점이나 더 재미있게 만들고 싶은 부분이 있나요? \uD83E\uDD14"
)

val START_WITH_DANNY = listOf(
    "어이구! 반갑습니다, 반가워요! \uD83E\uDD20 제가 바로 뛰는 재미 알려드릴 대니 러닝 코치입니다!",
    "경상도에서 온 제 말투가 좀 촌스럽습니까? 뭐 어쩌겠습니까, 러닝하는데 말투가 중요합니까? 하하!",
    "자, 이제 고객님 얘기 좀 해주시겠습니까? 재밌는 러닝 계획 짜보려는데, 고객님 사정을 좀 알아야 제대로 도와드리지 않겠습니까? \uD83C\uDFC3\u200D♂\uFE0F\uD83D\uDCA8",
    "고객님 목표가 어떻게 되십니까?"
)

val MODIFY_WITH_DANNY = listOf(
    "어이구! 또 오셨습니까? \uD83D\uDE1C 우리 고객님, 플랜 좀 바꿔보고 싶다 이 말입니까?",
    "아이고, 참 잘하셨습니다! 뭐가 마음에 안 드십니까? 달리기가 너무 쉽습니까? 하하하! \uD83E\uDD23",
    "자, 이제 고객님 얘기 좀 들어봅시다. 뭐가 문제였는지, 뭐를 바꾸고 싶은지 말씀해 보이소. \uD83D\uDC4D",
    "힘든 게 있었습니까? 아니면 더 재밌게 하고 싶은 게 있습니까? 들어보이소, 대니 코치가 다 해결해 드리겠습니다! \uD83D\uDCAA"
)

val WEEK_LIST = listOf(
    "Monday" to "월",
    "Tuesday" to "화",
    "Wednesday" to "수",
    "Thursday" to "목",
    "Friday" to "금",
    "Saturday" to "토",
    "Sunday" to "일"
)
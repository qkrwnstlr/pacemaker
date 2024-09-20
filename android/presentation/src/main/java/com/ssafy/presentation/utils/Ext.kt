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

fun String.toGenderIndex(): Int? = when (this) {
    WOMAN -> 0
    MAN -> 1
    else -> null
}

fun Long?.toCoachIndex(): Int = when (this) {
    1L -> R.drawable.coach_mike
    2L -> R.drawable.coach_jamie
    3L -> R.drawable.coach_danny
    else -> R.drawable.runnerfull
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

package com.ssafy.presentation.myPageUI.data

import android.os.Parcelable
import com.ssafy.domain.dto.User
import com.ssafy.presentation.utils.toAge
import com.ssafy.presentation.utils.toYear
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profile(
    val uid: String = "",
    val name: String = "",
    val age: Int = 0,
    val height: Int = 0,
    val weight: Int = 0,
    val gender: String? = "UNKNOWN"
) : Parcelable

fun User.toProfile(): Profile = Profile(
    uid = uid,
    name = name,
    age = year.toAge(),
    height = height,
    weight = weight,
    gender = gender
)

fun Profile.toUser(): User = User(
    uid = uid,
    name = name,
    year = age.toYear(),
    height = height,
    weight = weight,
    gender = gender ?: "UNKNOWN"
)

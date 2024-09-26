package com.ssafy.pacemaker.presentation.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Primary = Color(0xFF5973FF)
val SecondPrimary = Color(0xFF889AFF)
val ThirdPrimary = Color(0xFFB1BDFF)
val FourthPrimary = Color(0xFFD9DFFF)
val FifthPrimary = Color(0xFFEFF2FF)
val ReversePrimary = Color(0xFFFF76A0)
val ReversePrimary2 = Color(0xFFE8348B)
val Gray = Color(0xFF9F9F9F)
val LightGray = Color(0xFFD5D5D5)

val LightColors = lightColorScheme(
    primary = Primary,
    secondary = SecondPrimary,
    tertiary = ThirdPrimary,
    onPrimary = White,
    onSecondary = Black,
    onTertiary = Black,
    background = FifthPrimary,
    surface = FourthPrimary,
    onBackground = Gray,
    onSurface = LightGray,
)
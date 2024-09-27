package com.ssafy.pacemaker.presentation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Exercise : Screen("exercise")
    data object Result : Screen("result")
}
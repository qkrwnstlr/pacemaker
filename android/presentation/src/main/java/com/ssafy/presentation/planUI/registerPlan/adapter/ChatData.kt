package com.ssafy.presentation.planUI.registerPlan.adapter

sealed class ChatData(val text: String) {
    class MyData(text: String) : ChatData(text)
    class CoachData(text: String, val coachIndex: Long) : ChatData(text)
}

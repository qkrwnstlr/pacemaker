package com.ssafy.presentation.planUI.registerPlan.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.presentation.databinding.ListItemStudentBinding

class MyChatViewHolder(private val binding: ListItemStudentBinding) : ViewHolder(binding.root) {

    fun bind(data: ChatData.MyData) = with(binding) {
        tvCoachTalk.text = data.text
    }

}

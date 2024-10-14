package com.ssafy.presentation.planUI.registerPlan.adapter

import androidx.recyclerview.widget.DiffUtil

class ChatItemCallBack : DiffUtil.ItemCallback<ChatData>() {

    override fun areItemsTheSame(oldItem: ChatData, newItem: ChatData): Boolean =
        oldItem.hashCode() == newItem.hashCode()

    override fun areContentsTheSame(oldItem: ChatData, newItem: ChatData): Boolean =
        oldItem == newItem

}

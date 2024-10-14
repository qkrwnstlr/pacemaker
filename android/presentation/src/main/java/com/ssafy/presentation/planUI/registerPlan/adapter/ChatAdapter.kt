package com.ssafy.presentation.planUI.registerPlan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.presentation.databinding.ListItemCoachBinding
import com.ssafy.presentation.databinding.ListItemStudentBinding

class ChatAdapter : ListAdapter<ChatData, ViewHolder>(ChatItemCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MINE -> makeMyChatViewHolder(inflater, parent)
            COACH -> makeCoachChatViewHolder(inflater, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        when (holder) {
            is MyChatViewHolder -> holder.bind(item as ChatData.MyData)
            is CoachChatViewHolder -> holder.bind(item as ChatData.CoachData)
        }
    }

    override fun getItemViewType(position: Int): Int = when (currentList[position]) {
        is ChatData.MyData -> MINE
        is ChatData.CoachData -> COACH
    }

    private fun makeCoachChatViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): CoachChatViewHolder {
        val binding = ListItemCoachBinding.inflate(inflater, parent, false)
        return CoachChatViewHolder(binding)
    }

    private fun makeMyChatViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): MyChatViewHolder {
        val binding = ListItemStudentBinding.inflate(inflater, parent, false)
        return MyChatViewHolder(binding)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)

        if (holder is CoachChatViewHolder) {
            holder.onRecycled()
        }
    }

    companion object {
        const val MINE = 0
        const val COACH = 1
    }
}

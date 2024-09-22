package com.ssafy.presentation.planUI.registerPlan.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.presentation.databinding.ListItemCoachBinding
import com.ssafy.presentation.utils.toCoachIndex

class CoachChatViewHolder(
    private val binding: ListItemCoachBinding,
) : ViewHolder(binding.root) {

    fun bind(data: ChatData.CoachData, isFirstItem: Boolean) = with(binding) {
        tvCoachTalk.text = data.text

        if (!isFirstItem) return@with
        ivCoach.setImageResource(data.coachIndex.toCoachIndex())
        cvCoach.visibility = View.VISIBLE
    }

}

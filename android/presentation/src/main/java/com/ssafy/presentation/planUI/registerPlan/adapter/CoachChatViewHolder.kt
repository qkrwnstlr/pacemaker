package com.ssafy.presentation.planUI.registerPlan.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.presentation.databinding.ListItemCoachBinding
import com.ssafy.presentation.planUI.registerPlan.RegisterPlanViewModel
import com.ssafy.presentation.utils.toCoachIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

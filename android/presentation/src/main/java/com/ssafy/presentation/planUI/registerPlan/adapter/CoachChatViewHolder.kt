package com.ssafy.presentation.planUI.registerPlan.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.presentation.databinding.ListItemCoachBinding
import com.ssafy.presentation.planUI.registerPlan.RegisterPlanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoachChatViewHolder(private val binding: ListItemCoachBinding) : ViewHolder(binding.root) {

    private var job: Job? = null

    fun bind(data: ChatData.CoachData) = with(binding) {

        if (data.text.startsWith(RegisterPlanViewModel.COACH_CHATTING)) {
            job = makeSequenceChat()
        } else {
            job?.cancel()
            job = null
        }

        tvCoachTalk.text = data.text
    }

    private fun makeSequenceChat(): Job = CoroutineScope(Dispatchers.Main).launch {
        while (true) {
            delay(500)

            val chat = binding.tvCoachTalk.text.toString()
            val dotCount = chat.count { it == '.' }
            val newText = if (dotCount < 5) "${chat}." else chat.substringBefore(".")
            binding.tvCoachTalk.text = newText
        }
    }

    fun onRecycled() {
        job?.cancel()
        job = null
    }

}

package com.ssafy.presentation.scheduleUI.schedule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ListItemCoachMessageBinding
import com.ssafy.presentation.databinding.TrainResultCoachCustomViewBinding
import com.ssafy.presentation.utils.toCoachIndexJust

class TrainResultCoachView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private val binding: TrainResultCoachCustomViewBinding by lazy {
        TrainResultCoachCustomViewBinding.bind(
            LayoutInflater.from(context)
                .inflate(R.layout.train_result_coach_custom_view, this, false)
        )
    }

    private fun initView() {
        addView(binding.root)
    }

    fun setList(messList: List<String>) {
        binding.llCoachMessages.removeAllViews()
        messList.forEach { message ->
            val messageBinding = ListItemCoachMessageBinding.inflate(
                LayoutInflater.from(context),
                binding.llCoachMessages,
                false
            )
            messageBinding.tvCoachTalk.text = message
            binding.llCoachMessages.addView(messageBinding.root)
        }
    }

    fun setCoachImage(coachNum: Long) {
        val resIdx = coachNum.toCoachIndexJust()
        binding.ivCoach.setImageResource(resIdx)
    }
}
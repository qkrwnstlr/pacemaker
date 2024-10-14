package com.ssafy.presentation.trainUI.chatCoach

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentChatCoachBinding

class ChatCoachFragment : BaseFragment<FragmentChatCoachBinding>(
    FragmentChatCoachBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() = with(binding) {
        val slideDown = AnimationUtils.loadAnimation(
            requireContext(),
            com.ssafy.presentation.R.anim.fade_slide_down
        )

        tvTitle.startAnimation(slideDown)
    }
}
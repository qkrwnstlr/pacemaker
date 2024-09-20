package com.ssafy.presentation.planUI.startPlan

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentStartPlanBinding

class StartPlanFragment : BaseFragment<FragmentStartPlanBinding>(
    FragmentStartPlanBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() = with(binding.baseLayout) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        tvExplain.apply {
            text = WANT_AI_COACH
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = GOOD
            startAnimation(slideUp)
        }

        fabRed.apply {
            visibility = View.VISIBLE
            text = BAD
            startAnimation(slideUp)
        }
    }

    private fun initListener() = with(binding.baseLayout) {
        fabBlue.setOnClickListener {
            // TODO 여기서 코치 유무 체킹 하고 넘어 가야함.
            val action = StartPlanFragmentDirections.actionStartPlanFragmentToRegisterPlanFragment()
            findNavController().navigate(action)
        }
    }

    companion object {
        const val WANT_AI_COACH = "AI 코칭을 받아볼까요?"
        const val GOOD = "좋아요!"
        const val BAD = "아니요, 다음에 알아볼게요"
    }
}
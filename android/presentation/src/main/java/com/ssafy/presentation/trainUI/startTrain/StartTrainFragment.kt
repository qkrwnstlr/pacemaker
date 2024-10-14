package com.ssafy.presentation.trainUI.startTrain

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentStartTrainBinding

class StartTrainFragment : BaseFragment<FragmentStartTrainBinding>(
    FragmentStartTrainBinding::inflate
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
            text = "일일 코치를 체험해 보세요!"
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = "네, 좋아요!"
            startAnimation(slideUp)
        }

        fabRed.apply {
            visibility = View.VISIBLE
            text = "아니요, 그냥 달릴래요"
            startAnimation(slideUp)
        }
    }

    private fun initListener() = with(binding.baseLayout) {
        fabBlue.setOnClickListener {
            // TODO 여기서 코치 유무 체킹 하고 넘어 가야함.
            moveToChatCoachFragment()
        }
    }

    private fun moveToChatCoachFragment() {
        val action = StartTrainFragmentDirections.actionStartTrainFragmentToChatCoachFragment()
        findNavController().navigate(action)
    }
}
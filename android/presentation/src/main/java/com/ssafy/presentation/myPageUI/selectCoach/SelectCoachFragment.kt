package com.ssafy.presentation.myPageUI.selectCoach

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentSelectCoachBinding

class SelectCoachFragment : BaseFragment<FragmentSelectCoachBinding>(
    FragmentSelectCoachBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() = with(binding) {
        val slideDown = AnimationUtils.loadAnimation(
            requireContext(),
            com.ssafy.presentation.R.anim.fade_slide_down
        )
        tvExplain.startAnimation(slideDown)
        clRed.startAnimation(slideDown)
        clYellow.startAnimation(slideDown)
        clBlue.startAnimation(slideDown)
    }

    private fun initListener() = with(binding) {
        // TODO 코치 변경 로직 추가!
        clRed.setOnClickListener {

            findNavController().popBackStack()
        }

        clYellow.setOnClickListener {


            findNavController().popBackStack()
        }

        clBlue.setOnClickListener {

            findNavController().popBackStack()
        }
    }
}
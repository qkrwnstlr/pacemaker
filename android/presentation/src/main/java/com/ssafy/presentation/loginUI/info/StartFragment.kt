package com.ssafy.presentation.loginUI.info

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentStartBinding

class StartFragment : BaseFragment<FragmentStartBinding>(FragmentStartBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() = with(binding.baseLayout) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        tvExplain.apply {
            text = "달리기를 시작해볼까요!"
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = "좋아요!"
            startAnimation(slideUp)
        }

    }

    private fun initListener() = with(binding.baseLayout) {
        fabBlue.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

}
package com.ssafy.presentation.loginUI.join

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentJoinBinding

class JoinFragment : BaseFragment<FragmentJoinBinding>(FragmentJoinBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() = with(binding.baseLayout) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        tvExplain.apply {
            text = "PaceMaker를 사용하려면 정보를 등록해주세요"
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = "등록하기"
            startAnimation(slideUp)
        }

    }

    private fun initListener() = with(binding.baseLayout) {
        fabBlue.setOnClickListener {
            val action = JoinFragmentDirections.actionJoinFragmentToInfoFragment()
            findNavController().navigate(action)
        }
    }

}
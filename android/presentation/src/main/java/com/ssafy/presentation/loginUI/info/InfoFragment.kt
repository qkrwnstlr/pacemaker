package com.ssafy.presentation.loginUI.info

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentInfoBinding

class InfoFragment : BaseFragment<FragmentInfoBinding>(FragmentInfoBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() = with(binding.baseLayout) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        tvExplain.apply {
            text = "달리기 정보를 아시나요?"
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = "네, 알고 있어요"
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
            val action = InfoFragmentDirections.actionInfoFragmentToInfoRegisterFragment()
            findNavController().navigate(action)
        }
    }
}
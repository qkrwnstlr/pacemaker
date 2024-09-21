package com.ssafy.presentation.loginUI.connect

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentConnectBinding

class ConnectFragment : BaseFragment<FragmentConnectBinding>(FragmentConnectBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() = with(binding.baseLayout) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        tvExplain.apply {
            text = "Health Connect를 연동하세요!"
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = "권한 설정하기"
            startAnimation(slideUp)
        }

        ivRunner.visibility = View.GONE
        ivHealthConnect.visibility = View.VISIBLE
    }

    private fun initListener() = with(binding.baseLayout) {
        fabBlue.setOnClickListener {
            val action = ConnectFragmentDirections.actionConnectFragmentToStartFragment()
            findNavController().navigate(action)
        }
    }
}
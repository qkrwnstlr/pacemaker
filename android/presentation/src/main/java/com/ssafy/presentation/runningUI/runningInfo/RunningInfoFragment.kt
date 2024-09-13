package com.ssafy.presentation.runningUI.runningInfo

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRunningInfoBinding

class RunningInfoFragment : BaseFragment<FragmentRunningInfoBinding>(
    FragmentRunningInfoBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() = with(binding) {
        btnPause.setOnClickListener {
            btnPlay.showAnimate(true)
            btnStop.showAnimate(true)
            btnPause.showAnimate(false)
        }

        btnPlay.setOnClickListener {
            btnPause.showAnimate(true)
            btnPlay.showAnimate(false)
            btnStop.showAnimate(false)
        }

        btnStop.setOnClickListener {
            showSnackStringBar("훈련 종료!")
        }

        btnMap.setOnClickListener {
            showSnackStringBar("지도로 화면 변경!")
        }
    }

    private fun ImageButton.showAnimate(isShow: Boolean) {
        val alphaValue = if (isShow) 1f else 0f
        animate().apply {
            duration = 300
            alpha(alphaValue)

            withStartAction {
                if (isShow) visibility = View.VISIBLE
            }

            withEndAction {
                if (!isShow) visibility = View.INVISIBLE
            }
        }
    }
}
package com.ssafy.presentation.loginUI.info

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentInfoRegisterBinding
import kotlinx.coroutines.launch

class InfoRegisterFragment : BaseFragment<FragmentInfoRegisterBinding>(
    FragmentInfoRegisterBinding::inflate
) {
    private val viewModel: InfoRegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCollect()
        initListener()
    }

    private fun initListener() = with(binding) {
        fabBlue.apply {
            text = "등록하기"
            setOnClickListener { viewModel.nextOrder() }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.orderState.collect { order ->
                when (order) {
                    DISTANCE -> registerDistance()
                    TIME -> registerTime()
                    PACE -> registerPace()
                    CADENCE -> registerCadence()
                    else -> register()
                }
            }
        }
    }

    private fun moveToInfo() {
        val action = InfoRegisterFragmentDirections.actionInfoRegisterFragmentToStartFragment()
        findNavController().navigate(action)
    }

    private fun register() = with(binding) {
        //TODO 비어있는 공간 확인 로직 추가!
        moveToInfo()
        viewModel.resetOrder()
    }

    private fun registerDistance() = with(binding) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)

        tvTitle.startAnimation(slideDown)
        tvOrder.startAnimation(slideDown)
        tvExplain.startAnimation(slideDown)
    }

    private fun registerTime() = with(binding) {
        tilTime.visibility = View.VISIBLE
        tvOrder.text = "(2/4)"
    }

    private fun registerPace() = with(binding) {
        tilPace.visibility = View.VISIBLE
        tvOrder.text = "(3/4)"
    }

    private fun registerCadence() = with(binding) {
        tilCadence.visibility = View.VISIBLE
        tvOrder.text = "(4/4)"
    }

    companion object {
        const val DISTANCE = 1
        const val TIME = 2
        const val PACE = 3
        const val CADENCE = 4
    }
}
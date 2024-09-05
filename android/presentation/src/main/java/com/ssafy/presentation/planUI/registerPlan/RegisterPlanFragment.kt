package com.ssafy.presentation.planUI.registerPlan

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRegisterPlanBinding
import kotlinx.coroutines.launch

class RegisterPlanFragment : BaseFragment<FragmentRegisterPlanBinding>(
    FragmentRegisterPlanBinding::inflate
) {
    private val viewModel: RegisterPlanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initCollect()
    }

    private fun initListener() = with(binding) {
        fabBlue.apply {
            text = "훈련 일정 생성"
            setOnClickListener { viewModel.nextOrder() }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.orderState.collect { order ->
                when (order) {
                    DISTANCE -> registerDistance()
                    TIME -> registerTime()
                    WEEK -> registerWeek()
                    else -> register()
                }
            }
        }
    }

    private fun moveToInfo() {

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
    }

    private fun registerTime() = with(binding) {
        tilTime.visibility = View.VISIBLE
        tvOrder.text = "(2/3)"
    }

    private fun registerWeek() = with(binding) {
        tvLikeWeek.visibility = View.VISIBLE
        clWeek.visibility = View.VISIBLE
        tvOrder.text = "(3/3)"
    }

    companion object {
        const val DISTANCE = 1
        const val TIME = 2
        const val WEEK = 3
    }
}
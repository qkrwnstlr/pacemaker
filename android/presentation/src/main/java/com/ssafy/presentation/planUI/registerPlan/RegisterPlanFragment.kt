package com.ssafy.presentation.planUI.registerPlan

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRegisterPlanBinding

class RegisterPlanFragment : BaseFragment<FragmentRegisterPlanBinding>(
    FragmentRegisterPlanBinding::inflate
) {
    private val viewModel: RegisterPlanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        val manager = requireActivity().supportFragmentManager
        ScheduleDialogFragment().show(manager, "ScheduleDialog")
    }

    private fun initView() = with(binding) {
        val slideDown = AnimationUtils.loadAnimation(
            requireContext(),
            com.ssafy.presentation.R.anim.fade_slide_down
        )

        tvTitle.startAnimation(slideDown)
        tvLikeWeek.startAnimation(slideDown)
        clWeek.startAnimation(slideDown)
    }

    private fun initListener() = with(binding) {
        ivSend.setOnClickListener {
            val text = etChat.text.toString()
            etChat.text = null
            showSnackStringBar(text)
        }
    }
}
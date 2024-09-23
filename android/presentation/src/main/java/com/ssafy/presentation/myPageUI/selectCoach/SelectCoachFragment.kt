package com.ssafy.presentation.myPageUI.selectCoach

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentSelectCoachBinding
import com.ssafy.presentation.myPageUI.selectCoach.SelectCoachViewModel.Companion.DANNY
import com.ssafy.presentation.myPageUI.selectCoach.SelectCoachViewModel.Companion.JAMIE
import com.ssafy.presentation.myPageUI.selectCoach.SelectCoachViewModel.Companion.MIKE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SelectCoachFragment : BaseFragment<FragmentSelectCoachBinding>(
    FragmentSelectCoachBinding::inflate
) {
    private val viewModel: SelectCoachViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() = with(binding) {
        val slideDown = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fade_slide_down
        )

        tvExplain.startAnimation(slideDown)
        clRed.startAnimation(slideDown)
        clYellow.startAnimation(slideDown)
        clBlue.startAnimation(slideDown)
    }

    private fun initListener() = with(binding) {
        val uid = getUid()
        clRed.setOnClickListener { viewModel.setCoach(uid, MIKE, next(), ::failToSetCoach) }
        clYellow.setOnClickListener { viewModel.setCoach(uid, JAMIE, next(), ::failToSetCoach) }
        clBlue.setOnClickListener { viewModel.setCoach(uid, DANNY, next(), ::failToSetCoach) }
    }

    private fun next(): suspend () -> Unit {
        val previousDestination = findNavController().previousBackStackEntry?.destination

        return when (previousDestination?.id) {
            R.id.startPlanFragment -> ::moveToRegisterPlanFragment
            R.id.profileFragment -> ::popBack
            else -> throw IllegalArgumentException(ILLEGAL_DESTINATION)
        }
    }

    private suspend fun moveToRegisterPlanFragment() = withContext(Dispatchers.Main) {
        val action = SelectCoachFragmentDirections.actionSelectCoachFragmentToRegisterPlanFragment()
        findNavController().navigate(action)
    }

    private suspend fun popBack() = withContext(Dispatchers.Main) {
        findNavController().popBackStack()
    }

    private suspend fun failToSetCoach(message: String) = withContext(Dispatchers.Main) {
        showSnackStringBar(message)
    }

    companion object {
        const val ILLEGAL_DESTINATION = "잘못된 목적지입니다."
    }
}

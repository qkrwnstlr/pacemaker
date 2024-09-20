package com.ssafy.presentation.planUI.startPlan

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentStartPlanBinding
import com.ssafy.presentation.utils.ERROR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class StartPlanFragment : BaseFragment<FragmentStartPlanBinding>(
    FragmentStartPlanBinding::inflate
) {
    private val viewModel: StartPlanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() = with(binding.baseLayout) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        tvExplain.apply {
            text = WANT_AI_COACH
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = GOOD
            startAnimation(slideUp)
        }

        fabRed.apply {
            visibility = View.VISIBLE
            text = BAD
            startAnimation(slideUp)
        }
    }

    private fun initListener() = with(binding.baseLayout) {
        fabBlue.setOnClickListener {
            viewModel.checkCoach(
                getUid(),
                ::moveToRegisterPlanFragment,
                ::moveToSelectCoachFragment,
                ::failToGetCoach
            )
        }

        fabRed.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private suspend fun moveToRegisterPlanFragment() = withContext(Dispatchers.Main) {
        val action = StartPlanFragmentDirections.actionStartPlanFragmentToRegisterPlanFragment()
        findNavController().navigate(action)
    }

    private suspend fun moveToSelectCoachFragment() = withContext(Dispatchers.Main) {
        val action = StartPlanFragmentDirections.actionStartPlanFragmentToSelectCoachFragment()
        findNavController().navigate(action)
    }

    private suspend fun failToGetCoach() = withContext(Dispatchers.Main) {
        showSnackStringBar(ERROR)
    }

    companion object {
        const val WANT_AI_COACH = "AI 코칭을 받아볼까요?"
        const val GOOD = "좋아요!"
        const val BAD = "아니요, 다음에 알아볼게요"
    }
}
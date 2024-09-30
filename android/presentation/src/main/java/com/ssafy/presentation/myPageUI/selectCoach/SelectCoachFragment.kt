package com.ssafy.presentation.myPageUI.selectCoach

import android.media.MediaPlayer
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
import com.ssafy.presentation.databinding.FragmentSelectCoachBinding
import com.ssafy.presentation.myPageUI.selectCoach.SelectCoachViewModel.Companion.DANNY
import com.ssafy.presentation.myPageUI.selectCoach.SelectCoachViewModel.Companion.JAMIE
import com.ssafy.presentation.myPageUI.selectCoach.SelectCoachViewModel.Companion.MIKE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class SelectCoachFragment : BaseFragment<FragmentSelectCoachBinding>(
    FragmentSelectCoachBinding::inflate
) {
    private val viewModel: SelectCoachViewModel by viewModels()
    private val mediaPlayer by lazy { MediaPlayer() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        initCollect()
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
        clRed.setOnClickListener { viewModel.selectCoach(MIKE) }
        clYellow.setOnClickListener { viewModel.selectCoach(JAMIE) }
        clBlue.setOnClickListener { viewModel.selectCoach(DANNY) }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectCoachState()
            collectVoiceEvent()
        }
    }

    private fun CoroutineScope.collectCoachState() = launch {
        viewModel.selectCoachState.collectLatest { (coachIndex, count) ->
            if (count == SELECT_MAX_COUNT) {
                viewModel.setCoach(coachIndex, next(), ::showSnackBar)
            }
        }
    }

    private fun CoroutineScope.collectVoiceEvent() = launch {
        viewModel.voiceEvent.collectLatest { voicePath ->
            val file = File(voicePath)
            if (!file.exists()) return@collectLatest

            showSnackBar(MAKE_TWICE)
            mediaPlayer.apply {
                reset()
                setDataSource(file.path)
                prepare()
                start()

                setOnCompletionListener {
                    reset()
                    file.delete()
                }
            }
        }
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

    companion object {
        const val ILLEGAL_DESTINATION = "잘못된 목적지입니다."
        const val SELECT_MAX_COUNT = 2
        const val MAKE_TWICE = "한번 더 선택하시면 코치가 저장됩니다."
    }
}

package com.ssafy.presentation.loginUI.join

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
import com.ssafy.presentation.databinding.FragmentJoinRegisterBinding
import com.ssafy.presentation.myPageUI.modify.GenderButtonSheetFragment
import com.ssafy.presentation.utils.MAN
import com.ssafy.presentation.utils.WOMAN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class JoinRegisterFragment : BaseFragment<FragmentJoinRegisterBinding>(
    FragmentJoinRegisterBinding::inflate
) {
    private val viewModel: JoinRegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initCollect()
    }

    private fun initListener() = with(binding) {
        tieGender.setOnClickListener {
            showGenderBottomSheet()
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectOrderState()
            collectGenderState()
        }
    }

    private fun CoroutineScope.collectOrderState() = launch {
        viewModel.orderState.collect { order ->
            when (order) {
                1 -> registerName()
                2 -> registerAge()
                3 -> registerGender()
                4 -> registerHeight()
                5 -> registerWeight()
                else -> register()
            }
        }
    }

    private fun CoroutineScope.collectGenderState() = launch {
        viewModel.genderState.collectLatest { gender ->
            if (gender == MAN) binding.tieGender.setText(MAN)
            else if (gender == WOMAN) binding.tieGender.setText(WOMAN)
        }
    }

    private fun moveToInfo() {
        val action = JoinRegisterFragmentDirections.actionJoinRegisterFragmentToInfoFragment()
        findNavController().navigate(action)
    }

    private fun register() = with(binding) {
        //TODO 비어있는 공간 확인 로직 추가!
        moveToInfo()
        viewModel.resetOrder()
    }

    private fun registerName() = with(binding) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)

        tvTitle.apply {
            text = "이름을 알려주세요"
            startAnimation(slideDown)
        }

        tvOrder.apply {
            text = "(1/5)"
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = "등록하기"
            setOnClickListener { viewModel.nextOrder() }
        }
    }

    private fun registerAge() = with(binding) {
        tilAge.visibility = View.VISIBLE
        tvExplain.visibility = View.VISIBLE
        tvTitle.text = "나이를 알려주세요"
        tvOrder.text = "(2/5)"
    }

    private fun registerGender() = with(binding) {
        showGenderBottomSheet()
        tilGender.visibility = View.VISIBLE
        tvTitle.text = "성별을 알려주세요"
        tvOrder.text = "(3/5)"
    }

    private fun registerHeight() = with(binding) {
        tilHeight.visibility = View.VISIBLE
        tvTitle.text = "키를 알려주세요"
        tvOrder.text = "(4/5)"
    }

    private fun registerWeight() = with(binding) {
        tilWeight.visibility = View.VISIBLE
        tvTitle.text = "몸무게를 알려주세요"
        tvOrder.text = "(5/5)"
    }

    private fun showGenderBottomSheet() = with(binding.tieGender) {
        GenderButtonSheetFragment(text.toString(), viewModel::setGender)
            .show(parentFragmentManager, null)
    }
}
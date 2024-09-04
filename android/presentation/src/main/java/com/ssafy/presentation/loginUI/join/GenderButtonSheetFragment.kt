package com.ssafy.presentation.loginUI.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.FragmentGenderButtonSheetBinding

class GenderButtonSheetFragment(
    private val gender: String,
    private val setGender: (String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentGenderButtonSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenderButtonSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() = with(binding) {
        val check = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_check_24)

        when (gender) {
            JoinRegisterFragment.WOMAN -> {
                tvWoman.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, check, null)
            }

            JoinRegisterFragment.MAN -> {
                tvMan.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, check, null)
            }
        }
    }

    private fun initListener() = with(binding) {
        tvWoman.setOnClickListener {
            setGender(JoinRegisterFragment.WOMAN)
            dismiss()
        }

        tvMan.setOnClickListener {
            setGender(JoinRegisterFragment.MAN)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
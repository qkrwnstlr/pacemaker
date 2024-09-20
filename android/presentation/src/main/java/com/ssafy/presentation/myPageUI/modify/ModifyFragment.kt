package com.ssafy.presentation.myPageUI.modify

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentModifyBinding
import com.ssafy.presentation.utils.toGender
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModifyFragment : BaseFragment<FragmentModifyBinding>(FragmentModifyBinding::inflate) {
    private val args: ModifyFragmentArgs by navArgs()
    private val viewModel: ModifyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        initView()
        initListener()
    }

    private fun setData() = viewModel.setNewProfile(args.profile)

    private fun initView() = with(viewModel.profile) {
        if (name.isNotBlank()) binding.tieName.setText(name)
        if (age != 0) binding.tieAge.setText(age.toString())
        if (height != 0) binding.tieHeight.setText(height.toString())
        if (weight != 0) binding.tieWeight.setText(weight.toString())
        if (gender != null) binding.tieGender.setText(gender.toGender())
    }

    private fun initListener() = with(binding) {
        tieName.addTextChangedListener { text ->
            val newName = text?.toString() ?: ""
            viewModel.setName(newName)
        }

        tieAge.addTextChangedListener { text ->
            val newAge = text?.toString()?.toIntOrNull() ?: 0
            viewModel.setAge(newAge)
        }

        tieHeight.addTextChangedListener { text ->
            val newHeight = text?.toString()?.toIntOrNull() ?: 0
            viewModel.setHeight(newHeight)
        }

        tieWeight.addTextChangedListener { text ->
            val newWeight = text?.toString()?.toIntOrNull() ?: 0
            viewModel.setWeight(newWeight)
        }

        tieGender.setOnClickListener {
            showGenderBottomSheet()
        }

        btnModify.setOnClickListener {
            val uid = getUid()
            viewModel.modifyProfile(uid, ::popBack, ::failToSetProfile)
        }
    }

    private fun showGenderBottomSheet() = with(binding.tieGender) {
        GenderButtonSheetFragment(text.toString(), ::setGender)
            .show(parentFragmentManager, null)
    }

    private fun setGender(gender: String) {
        binding.tieGender.setText(gender)
        viewModel.setGender(gender)
    }

    private fun popBack() = findNavController().popBackStack()
    private fun failToSetProfile(message: String) = showSnackStringBar(message)

}

package com.ssafy.presentation.scheduleUI.planDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.DialogDeletePlanBinding

class PlanDeleteDialog : DialogFragment() {
    private var _binding: DialogDeletePlanBinding? = null
    private val binding: DialogDeletePlanBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDeletePlanBinding.inflate(layoutInflater, container, false)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_background_white)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() = with(binding) {
        btnOk.setOnClickListener { }
        btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
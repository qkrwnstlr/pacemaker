package com.ssafy.presentation.scheduleUI.planDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.DialogSelectWeekBinding

class PlanModifyDialog : DialogFragment() {
    private var _binding: DialogSelectWeekBinding? = null
    private val binding: DialogSelectWeekBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectWeekBinding.inflate(inflater, container, false)

        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_background_white)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() = with(binding) {
        btnOk.setOnClickListener {
            // TODO 고차함수 넘겨서 로직 처리
        }

        btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
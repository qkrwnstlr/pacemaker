package com.ssafy.presentation.scheduleUI.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.DialogPostponeBinding

class PostPoneDialog(
    val onYesButtonClick: (View) -> Unit
) : DialogFragment() {
    private var _binding: DialogPostponeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPostponeBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnOk.setOnClickListener {
            onYesButtonClick(view)
        }
        return view
    }


    override fun onResume() {
        super.onResume()

        dialog?.window?.let {
            it.attributes.apply {
                width = (resources.displayMetrics.widthPixels * 0.85).toInt()
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            it.setBackgroundDrawableResource(R.drawable.rounded_background_white)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
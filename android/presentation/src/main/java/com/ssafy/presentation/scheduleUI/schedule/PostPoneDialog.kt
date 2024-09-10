package com.ssafy.presentation.scheduleUI.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.DialogPostponeBinding

class PostPoneDialog(
    postponeDialogInterface: PostPoneDialogInterface,
) : DialogFragment() {
    private var _binding: DialogPostponeBinding? = null
    private val binding get() = _binding!!

    private var postponeDialogInterface: PostPoneDialogInterface? = null

    init {
        this.postponeDialogInterface = postponeDialogInterface
    }

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
            this.postponeDialogInterface?.onYesButtonClick(view)
        }
        return view
    }

    override fun onResume() {
        super.onResume()

        // Dialog 크기 조정
        val window = dialog?.window
        val params = window?.attributes
        params?.width = (resources.displayMetrics.widthPixels * 0.85).toInt()  // 화면 너비의 85%
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT  // 높이는 wrap_content로 설정
        window?.attributes = params
        window?.setBackgroundDrawableResource(R.drawable.rounded_background_white)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface PostPoneDialogInterface {
    fun onYesButtonClick(pview: View) {
        Snackbar.make(pview, "ok버튼 클릭", Snackbar.LENGTH_SHORT).show()
    }
}
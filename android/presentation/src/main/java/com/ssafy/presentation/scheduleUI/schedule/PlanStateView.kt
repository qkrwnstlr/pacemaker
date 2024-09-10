package com.ssafy.presentation.scheduleUI.schedule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.PlanCustomViewBinding

class PlanStateView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private val binding: PlanCustomViewBinding by lazy {
        PlanCustomViewBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.plan_custom_view, this, false)
        )
    }

    private fun initView() {
        addView(binding.root)
    }
}
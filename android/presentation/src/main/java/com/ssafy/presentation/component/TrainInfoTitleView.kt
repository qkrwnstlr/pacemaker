package com.ssafy.presentation.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.TrainInfoTitleCustomViewBinding
import com.ssafy.presentation.utils.toLocalDateDot

class TrainInfoTitleView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private val binding: TrainInfoTitleCustomViewBinding by lazy {
        TrainInfoTitleCustomViewBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.train_info_title_custom_view, this, false)
        )
    }

    private fun initView() {
        addView(binding.root)
    }

    fun setTitle(date: String, content: String = context.getString(R.string.nothing)) =
        with(binding) {
            tvResultTitle.text = date.toLocalDateDot()
            tvPlanInst.text = content
        }
}
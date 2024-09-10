package com.ssafy.presentation.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.TrainInfoChartCustomViewBinding
import com.ssafy.presentation.databinding.TrainInfoCustomViewBinding

class TrainInfoChartView : ConstraintLayout {
  constructor(context: Context) : super(context) {
    initView()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initView()
  }

  private val binding: TrainInfoChartCustomViewBinding by lazy {
    TrainInfoChartCustomViewBinding.bind(
      LayoutInflater.from(context).inflate(R.layout.train_info_chart_custom_view, this, false)
    )
  }

  private fun initView() {
    addView(binding.root)
  }
}
package com.ssafy.presentation.scheduleUI.schedule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.HeartProgressBinding

class HeartView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private val binding: HeartProgressBinding by lazy {
        HeartProgressBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.heart_progress, this, false)
        )
    }

    private fun initView() {
        addView(binding.root)
    }

    fun setData(heartList: List<Int>) = with(binding) {
        pbZone1.progress = heartList[0]
        pbZone2.progress = heartList[1]
        pbZone3.progress = heartList[2]
        pbZone4.progress = heartList[3]
        pbZone5.progress = heartList[4]
    }
}
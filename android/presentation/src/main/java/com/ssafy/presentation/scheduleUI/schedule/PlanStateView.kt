package com.ssafy.presentation.scheduleUI.schedule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.PlanCustomViewBinding
import com.ssafy.presentation.utils.makeProgressString

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

    fun setPlanData(
        planString: String,
        current: Int,
        total: Int,
        state: String,
        totalDistance: Int,
        totalTime: Int
    ) {
        binding.tvPlanInst.text = makeProgressString(planString,total)
        val progressPercentage = (current.toFloat() / total * 100).toInt()
        binding.progressBar.progress = progressPercentage

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root)
        constraintSet.setHorizontalBias(R.id.iv_progress, progressPercentage / 100f)

        constraintSet.applyTo(binding.root)

        when (state) {
            "ACTIVE" -> {
                binding.root.isVisible = true
            }

            "COMPLETED" -> {
                binding.progressBar.progressDrawable =
                    ContextCompat.getDrawable(context, R.drawable.progress_bar_percent_done)
//                binding.ivProgress.setImageResource(R.id.)todo:색 전환된 progressman
            }

            "DELETED" -> {
                binding.progressBar.progressDrawable =
                    ContextCompat.getDrawable(context, R.drawable.progress_bar_percent_done)
            }
        }
    }
}
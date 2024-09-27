package com.ssafy.presentation.scheduleUI.schedule

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.TrainResultCustomViewBinding
import com.ssafy.presentation.utils.formatCadenceRate
import com.ssafy.presentation.utils.formatHeartRate
import com.ssafy.presentation.utils.formatPace
import com.ssafy.presentation.utils.toDistanceString
import com.ssafy.presentation.utils.toTime

class TrainResultView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private val binding: TrainResultCustomViewBinding by lazy {
        TrainResultCustomViewBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.train_result_custom_view, this, false)
        )
    }

    private fun initView() {
        addView(binding.root)
    }

    fun setResultData(distance: Int, time: Int, hearRate: Int, cadence: Int, pace: Int, kcal: Int) {
        binding.tvResultDistanceContent.text = distance.toDistanceString()
        binding.tvResultHeartContent.text = formatHeartRate(hearRate.toDouble())
        binding.tvResultTimeContent.text = time.toTime()
        binding.tvResultStepContent.text = formatCadenceRate(cadence)
        binding.tvResultPaceContent.text = formatPace(pace.toDouble())
        binding.tvResultKcalContent.text = formatCadenceRate(kcal)
    }

    fun setPieChart(pacePercent: Float, heartPercent: Float, stepPercent: Float) = with(binding) {
        val colors = listOf(
            Color.parseColor("#FFFFFFFF"),
            Color.parseColor("#5973FF"),
        )

        setupPieChart(chartPace, pacePercent, 100f, colors)
        setupPieChart(chartHeart, heartPercent, 100f, colors)
        setupPieChart(chartStep, stepPercent, 100f, colors)

    }

    private fun setupPieChart(
        chart: PieChart,
        valuePercent: Float,
        totalPercent: Float,
        colors: List<Int>
    ) {
        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(totalPercent - valuePercent))
            add(PieEntry(valuePercent, ""))
        }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            setDrawValues(false)
        }

        chart.apply {
            data = PieData(dataSet)
            holeRadius = 75f
            transparentCircleRadius = 75f
            setDrawCenterText(false)
            legend.isEnabled = false
            description.isEnabled = false
            invalidate()
        }
    }
}
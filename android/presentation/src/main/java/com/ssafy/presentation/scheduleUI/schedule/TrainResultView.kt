package com.ssafy.presentation.scheduleUI.schedule

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.ssafy.domain.dto.reports.TrainReport
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.TrainResultCustomViewBinding
import com.ssafy.presentation.utils.formatCadenceRate
import com.ssafy.presentation.utils.formatCalories
import com.ssafy.presentation.utils.formatDistanceKm
import com.ssafy.presentation.utils.formatElapsedTime
import com.ssafy.presentation.utils.formatHeartRate
import com.ssafy.presentation.utils.formatPace
import com.ssafy.presentation.utils.toRank
import com.ssafy.presentation.utils.toRankColor
import java.time.Duration

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
        binding.tvResultDistanceContent.text = formatDistanceKm(distance.toDouble())
        binding.tvResultHeartContent.text = formatHeartRate(hearRate.toDouble())
        binding.tvResultTimeContent.text = formatElapsedTime(Duration.ofSeconds(time.toLong()), true)
        binding.tvResultStepContent.text = formatCadenceRate(cadence)
        binding.tvResultPaceContent.text = formatPace(pace.toDouble())
        binding.tvResultKcalContent.text = formatCalories(kcal.toDouble())
    }

    fun setResultData(trainReport: TrainReport) {
        binding.tvResultDistanceContent.text = formatDistanceKm(trainReport.trainResult.trainDistance.toDouble())
        binding.tvResultHeartContent.text = formatHeartRate(trainReport.trainResult.heartRate.toDouble())
        binding.tvResultTimeContent.text = formatElapsedTime(Duration.ofSeconds(trainReport.trainResult.trainTime.toLong()), true)
        binding.tvResultStepContent.text = formatCadenceRate(trainReport.trainResult.cadence)
        binding.tvResultPaceContent.text = formatPace(trainReport.trainResult.pace.toDouble())
        binding.tvResultKcalContent.text = formatCalories(trainReport.trainResult.kcal.toDouble())
        if (trainReport.trainEvaluation != null) {
            visibleChart()
            trainReport.trainEvaluation?.apply {
                setPieChart(
                    paceEvaluation.toFloat(),
                    heartRateEvaluation.toFloat(),
                    cadenceEvaluation.toFloat()
                )
            }
        } else {
            unvisibleChart()
        }
    }

    fun unvisibleChart() {
        binding.lyChart.isVisible = false
    }

    fun visibleChart() {
        binding.lyChart.isVisible = true
    }

    fun setPieChart(pacePercent: Float, heartPercent: Float, stepPercent: Float) = with(binding) {
        visibleChart()

        setupPieChart(chartPace, pacePercent)
        setupPieChart(chartHeart, heartPercent)
        setupPieChart(chartStep, stepPercent)
        setupPieText(rankPace, pacePercent)
        setupPieText(rankHeart, heartPercent)
        setupPieText(rankStep, stepPercent)
    }

    private fun setupPieText(tv: TextView, valuePercent: Float) {
        tv.text = valuePercent.toRank()
    }

    private fun setupPieChart(
        chart: PieChart,
        valuePercent: Float,
    ) {
        val colors = listOf(
            Color.parseColor("#FFFFFFFF"),
            Color.parseColor(valuePercent.toRankColor()),
        )
        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(100 - valuePercent))
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
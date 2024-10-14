package com.ssafy.presentation.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.TrainInfoChartCustomViewBinding
import com.ssafy.presentation.planUI.registerPlan.ScheduleDialogFragment.Companion.DISTANCE
import com.ssafy.presentation.planUI.registerPlan.ScheduleDialogFragment.Companion.INCREASE_X
import com.ssafy.presentation.planUI.registerPlan.ScheduleDialogFragment.Companion.LARGE_Y
import com.ssafy.presentation.planUI.registerPlan.ScheduleDialogFragment.Companion.SMALL_Y
import com.ssafy.presentation.planUI.registerPlan.ScheduleDialogFragment.Companion.TIME
import com.ssafy.presentation.utils.toTrainText

class TrainInfoChartView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    val binding: TrainInfoChartCustomViewBinding by lazy {
        TrainInfoChartCustomViewBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.train_info_chart_custom_view, this, false)
        )
    }

    private fun initView() {
        addView(binding.root)
    }

    fun makeEntryChart(planTrain: PlanTrain?) {
        makeChart(makeBarEntries(planTrain))
        binding.tvMid.text = planTrain?.toTrainText()

    }

    private fun makeBarEntries(planTrain: PlanTrain?): MutableList<BarEntry> {
        return planTrain?.let {
            val entries = mutableListOf<BarEntry>()
            repeat(5) { entries.add(BarEntry(entries.size + INCREASE_X, SMALL_Y)) }

            val divider = when (it.paramType) {
                TIME -> 60
                DISTANCE -> 100
                else -> 1
            }
            val trainCount = it.trainParam / divider
            val interCount = it.interParam / divider

            repeat(trainCount) {
                val entry = BarEntry(entries.size + INCREASE_X, LARGE_Y)
                entries.add(entry)
            }

            repeat(it.repetition - 1) {
                repeat(interCount) {
                    val entry = BarEntry(entries.size + INCREASE_X, SMALL_Y)
                    entries.add(entry)
                }
                repeat(trainCount) {
                    val entry = BarEntry(entries.size + INCREASE_X, LARGE_Y)
                    entries.add(entry)
                }
            }

            repeat(5) { entries.add(BarEntry(entries.size + INCREASE_X, SMALL_Y)) }

            entries
        } ?: mutableListOf()
    }

    private fun makeChart(entries: MutableList<BarEntry>) {
        val barChart: BarChart = binding.barChart

        barChart.apply {
            description.isEnabled = false
            setMaxVisibleValueCount(entries.size)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            axisLeft.apply {
                axisMaximum = 1f
                axisMinimum = 0f
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawBarShadow(false)
                setDrawGridLines(false)
                setDrawLabels(false)
            }

            axisRight.isEnabled = false
            setTouchEnabled(false)
            animateY(300)
            legend.isEnabled = false
        }
        val set = BarDataSet(entries, "DataSet")

        val colors = List(entries.size) { index ->
            if (entries[index].y < 1f) R.color.thirdPrimary else R.color.secondPrimary
        }

        set.setColors(colors.toIntArray(), barChart.context)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        val data = BarData(dataSet)
        data.barWidth = 1.01f //막대 너비 설정
        barChart.data = data
    }
}
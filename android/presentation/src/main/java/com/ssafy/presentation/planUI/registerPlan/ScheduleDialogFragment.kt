package com.ssafy.presentation.planUI.registerPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.FragmentScheduleDialogBinding
import com.ssafy.presentation.utils.displayText
import com.ssafy.presentation.utils.toPlanInst
import com.ssafy.presentation.utils.toTrainText
import java.time.LocalDate

class ScheduleDialogFragment(
    private val date: LocalDate,
    private val planTrain: PlanTrain? = null
) : DialogFragment() {
    private var _binding: FragmentScheduleDialogBinding? = null
    private val binding: FragmentScheduleDialogBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_background_white)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        trainInfoTitle.ivNext.visibility = View.GONE
        root.layoutParams.width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        trainInfoTitle.tvResultTitle.text = date.displayText()
        trainInfoTitle.tvPlanInst.text = planTrain?.toPlanInst()
        trainInfoChart.tvMid.text = planTrain?.toTrainText()

        val entries = makeBarEntries()
        makeChart(trainInfoChart.barChart, entries)
    }

    private fun makeBarEntries(): MutableList<BarEntry> {
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

            repeat(it.repeat - 1) {
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

    private fun makeChart(barChart: BarChart, entries: MutableList<BarEntry>) {

        barChart.apply {
            description.isEnabled = false
            setMaxVisibleValueCount(entries.size)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            axisLeft.apply {
                axisMaximum = 2f
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

    companion object {
        const val TIME = "time"
        const val DISTANCE = "distance"
        const val INCREASE_X = 1.5f
        const val SMALL_Y = 0.2f
        const val LARGE_Y = 1.2f
    }
}
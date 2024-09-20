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
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.FragmentScheduleDialogBinding
import com.ssafy.presentation.utils.displayText
import java.time.LocalDate

class ScheduleDialogFragment(
    private val date: LocalDate
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
        makeChart(trainInfoChart.barChart)
    }

    private fun makeChart(barChart: BarChart) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(2.5f, 0.2f))
        entries.add(BarEntry(3.5f, 1.6f))
        entries.add(BarEntry(4.5f, 1.2f))
        entries.add(BarEntry(5.5f, 1.6f))
        entries.add(BarEntry(6.5f, 1.2f))
        entries.add(BarEntry(7.5f, 1.6f))
        entries.add(BarEntry(8.5f, 0.2f))

        barChart.apply {
            description.isEnabled = false
            setMaxVisibleValueCount(7)
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
            if (index % 2 == 0) R.color.thirdPrimary else R.color.secondPrimary
        }

        set.setColors(colors.toIntArray(), barChart.context)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        val data = BarData(dataSet)
        data.barWidth = 0.8f //막대 너비 설정
        barChart.data = data
    }

}
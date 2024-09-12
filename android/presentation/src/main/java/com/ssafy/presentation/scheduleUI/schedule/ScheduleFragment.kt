package com.ssafy.presentation.scheduleUI.schedule

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentScheduleBinding
import com.ssafy.presentation.utils.displayText
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class ScheduleFragment : BaseFragment<FragmentScheduleBinding>(FragmentScheduleBinding::inflate) {
    private val monthCalendarView: CalendarView get() = binding.exOneCalendar

    private var selectedDate = LocalDate.now()

    private var predate = LocalDate.now()

    private val today = LocalDate.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val daysOfWeek = daysOfWeek()
        binding.legendLayout.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText(narrow = true)
            }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)

        binding.btnNextMonth.setOnClickListener {
            binding.exOneCalendar.findFirstVisibleMonth()?.let {
                binding.exOneCalendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.btnPrevMonth.setOnClickListener {
            binding.exOneCalendar.findFirstVisibleMonth()?.let {
                binding.exOneCalendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
        val trainInfoView = binding.lyResultInfo
        val barChart: BarChart = trainInfoView.findViewById(R.id.barChart)
        makeChart(barChart)
        makeResult()
        val dialog = PostponeDialog(::onYesButtonClick)
        dialog.show(requireActivity().supportFragmentManager, "ConfirmDialog")

        initListener()
    }

    private fun initListener() = with(binding) {
        lyPlan.setOnClickListener { moveToPlanDetailFragment() }
    }

    private fun moveToPlanDetailFragment() {
        val action = ScheduleFragmentDirections.actionScheduleFragmentToPlanDetailFragment2()
        findNavController().navigate(action)
    }

    private fun onYesButtonClick() {
        showSnackStringBar("ok버튼 클릭")
    }

    private fun makeResult() {
        // 전체
        val totalPercent = 100f
        val pacePercent = 75f
        val heartPercent = 60f
        val stepPercent = 70f

        val paceChart: PieChart = binding.lyTrainResult.findViewById(R.id.chart_pace)
        val heartChart: PieChart = binding.lyTrainResult.findViewById(R.id.chart_heart)
        val stepChart: PieChart = binding.lyTrainResult.findViewById(R.id.chart_step)

        val paceEntry = ArrayList<PieEntry>()
        paceEntry.add(PieEntry(totalPercent - pacePercent))
        paceEntry.add(PieEntry(pacePercent, ""))

        val heartEntry = ArrayList<PieEntry>()
        heartEntry.add(PieEntry(totalPercent - heartPercent))
        heartEntry.add(PieEntry(heartPercent, ""))

        val stepEntry = ArrayList<PieEntry>()
        stepEntry.add(PieEntry(totalPercent - stepPercent))
        stepEntry.add(PieEntry(stepPercent, ""))

        // 그래프 색상(데이터 순서)
        val colors = listOf(
            Color.parseColor("#FFFFFFFF"),
            Color.parseColor("#5973FF"),
        )

        val paceDataSet = PieDataSet(paceEntry, "")
        paceDataSet.colors = colors
        paceDataSet.setDrawValues(false)

        val heartDataSet = PieDataSet(heartEntry, "")
        heartDataSet.colors = colors
        heartDataSet.setDrawValues(false)

        val stepDataSet = PieDataSet(stepEntry, "")
        stepDataSet.colors = colors
        stepDataSet.setDrawValues(false)

        // Pie 그래프 생성
        val dataPaceChart = PieData(paceDataSet)
        paceChart.apply {
            data = dataPaceChart
            holeRadius = 75f
            transparentCircleRadius = 75f
            setDrawCenterText(false)
            legend.isEnabled = false
            description.isEnabled = false
        }

        val dataHeartChart = PieData(heartDataSet)
        heartChart.apply {
            data = dataHeartChart
            holeRadius = 75f
            transparentCircleRadius = 75f
            setDrawCenterText(false)
            legend.isEnabled = false
            description.isEnabled = false
        }

        val dataStepChart = PieData(stepDataSet)
        stepChart.apply {
            data = dataStepChart
            holeRadius = 75f
            transparentCircleRadius = 75f
            setDrawCenterText(false)
            legend.isEnabled = false
            description.isEnabled = false
        }

        // 그래프 업데이트
        paceChart.invalidate()
        heartChart.invalidate()
        stepChart.invalidate()
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
            animateY(1000)
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
        barChart.apply {
            this.data = data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        monthCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view, ::dateClicked)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(
                    data.date,
                    container.textView,
                    container.ly,
                    container.exThreeDotView,
                    data.position == DayPosition.MonthDate
                )
            }
        }
        monthCalendarView.monthScrollListener = { updateTitle() }
        monthCalendarView.setup(startMonth, endMonth, daysOfWeek.first())
        monthCalendarView.scrollToMonth(currentMonth)
    }

    private fun bindDate(
        date: LocalDate,
        textView: TextView,
        layout: ConstraintLayout,
        hasTrain: View,
        isSelectable: Boolean
    ) {
        if (isSelectable) {
            textView.text = date.dayOfMonth.toString()
            when {
                selectedDate == date -> {
                    layout.setBackgroundResource(R.drawable.day_selected_bg)
                }

                today == date -> {
                    layout.setBackgroundResource(R.drawable.day_today_bg)
                }

                else -> {
                    layout.background = null
                }
            }
        } else {
            layout.background = null
            hasTrain.visibility = View.GONE
        }
    }

    private fun dateClicked(date: LocalDate) {
        predate = selectedDate
        selectedDate = date
        // Refresh both calendar views..
        monthCalendarView.notifyDateChanged(predate)
        monthCalendarView.notifyDateChanged(date)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val month = monthCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.exOneYearText.text = month.year.toString()
        binding.exOneMonthText.text = month.month.displayText(short = true)
    }
}

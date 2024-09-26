package com.ssafy.presentation.scheduleUI.schedule

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.ssafy.domain.dto.schedule.Content
import com.ssafy.domain.dto.schedule.ProgressData
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentScheduleBinding
import com.ssafy.presentation.utils.displayText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@AndroidEntryPoint
class ScheduleFragment : BaseFragment<FragmentScheduleBinding>(FragmentScheduleBinding::inflate) {
    private val viewModel: ScheduleViewModel by viewModels()
    private val monthCalendarView: CalendarView get() = binding.exOneCalendar

    private var selectedDate = LocalDate.now()
    private var predate = LocalDate.now()
    private val today = LocalDate.now()

    private var currentMonth = YearMonth.now()
    private val startMonth = currentMonth.minusMonths(100)
    private val endMonth = currentMonth.plusMonths(100)
    private val daysOfWeek = daysOfWeek()
    private lateinit var planStateView: PlanStateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.legendLayout.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText(narrow = true)
            }

        viewModel.setMonthHasTrain(getUid(), currentMonth.year, currentMonth.monthValue)
        setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
        initCollect()

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

        initListener()
        planStateView = binding.lyPlan
        viewModel.dateProgressInfo(selectedDate,::setProgress)

    }

    private fun setProgress(progressData: ProgressData){
        if(progressData.status=="NOTHING"){
            planStateView.isVisible=false
        }
        else{
            planStateView.setPlanData(progressData.goal,progressData.completedCount,progressData.totalDays,progressData.status)
        }
    }

    private fun initListener() = with(binding) {
        lyPlan.setOnClickListener { moveToPlanDetailFragment() }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                var previousDotList = emptyList<LocalDate>()
                viewModel.dotList.collectLatest { newDotList ->
                    val changedDates = newDotList.filterNot { previousDotList.contains(it) } +
                            previousDotList.filterNot { newDotList.contains(it) }

                    for (date in changedDates) {
                        monthCalendarView.notifyDateChanged(date)
                    }
                    previousDotList = newDotList
                }
            }
        }
    }

    private fun moveToPlanDetailFragment() {
        val action = ScheduleFragmentDirections.actionScheduleFragmentToPlanDetailFragment2()
        findNavController().navigate(action)
    }

    private fun makeResult() {
        val pacePercent = 75f
        val heartPercent = 60f
        val stepPercent = 70f

        val trainResultView = binding.lyTrainResult
        trainResultView.setPieChart(pacePercent,heartPercent,stepPercent)
    }

    private fun makeChart(barChart: BarChart) {
        val entries = ArrayList<BarEntry>().apply {
            add(BarEntry(2.5f, 0.2f))
            add(BarEntry(3.5f, 1.6f))
            add(BarEntry(4.5f, 1.2f))
            add(BarEntry(5.5f, 1.6f))
            add(BarEntry(6.5f, 1.2f))
            add(BarEntry(7.5f, 1.6f))
            add(BarEntry(8.5f, 0.2f))
        }

        val barDataSet = BarDataSet(entries, "DataSet").apply {
            val colors = List(entries.size) { index ->
                if (index % 2 == 0) R.color.thirdPrimary else R.color.secondPrimary
            }
            setColors(colors.toIntArray(), barChart.context)
        }

        barChart.apply {
            data = BarData(barDataSet).apply { barWidth = 0.8f }
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
            invalidate()
        }
    }
    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>
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
                    data.position == DayPosition.MonthDate,
                    viewModel.dotList.value.contains(data.date)
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
        isSelectable: Boolean,
        isVisibleTrain: Boolean
    ) {
        if (isSelectable) {
            textView.text = date.dayOfMonth.toString()
            layout.setBackgroundResource(
                when {
                    selectedDate == date -> R.drawable.day_selected_bg
                    today == date -> R.drawable.day_today_bg
                    else -> 0
                }
            )
        } else {
            layout.background = null
        }
        hasTrain.visibility = if (isVisibleTrain) View.VISIBLE else View.INVISIBLE

    }

    private fun dateClicked(date: LocalDate) {
        predate = selectedDate
        selectedDate = date
        monthCalendarView.notifyDateChanged(predate)
        monthCalendarView.notifyDateChanged(date)
        viewModel.dateList.value.let {
            for(i in it.indices){
                if(LocalDate.parse(it[i].date)==selectedDate){
                    setResultView(it[i].contentList)
                    break
                }
                if(i==it.size-1){
                    setResultView(emptyList())
                }
            }
        }
    }

    private fun setResultView(contentList: List<Content>) {
        if (contentList.isEmpty()) {//쉬는날
            binding.lyResultInfo.isVisible = false
            binding.lyTrainResult.isVisible = false
            binding.map.isVisible = false
            binding.lyTrainResultCoach.isVisible = false
            binding.lyNothing.isVisible = true
        } else {
            binding.lyNothing.isVisible = false
            binding.lyResultInfo.isVisible = true
            binding.lyTrainResult.isVisible = true
            binding.map.isVisible = true
            binding.lyTrainResultCoach.isVisible = true
            Timber.d("${contentList[0]} ${contentList[1]}")
            val trainInfoView = binding.lyResultInfo
            val barChart: BarChart = trainInfoView.findViewById(R.id.barChart)
            makeChart(barChart)
            makeResult()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val month = monthCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.exOneYearText.text = month.year.toString()
        binding.exOneMonthText.text = month.month.displayText(short = true)
    }
}

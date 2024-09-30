package com.ssafy.presentation.scheduleUI.schedule

import android.annotation.SuppressLint
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
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.ssafy.domain.dto.schedule.ContentListDto
import com.ssafy.domain.dto.schedule.ProgressData
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentScheduleBinding
import com.ssafy.presentation.scheduleUI.schedule.pager.ViewPagerAdapter
import com.ssafy.presentation.utils.displayText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private var prevDateMap = emptyMap<String, List<ContentListDto>>()
    private var flag = true
    private val adapter = ViewPagerAdapter(emptyList()) { item, callback ->
        viewModel.getReport(item, getUid()) { report ->
            callback(report)
        }
    }

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
        initListener()

        binding.vpReport.adapter = adapter

        planStateView = binding.lyPlan
        viewModel.dateProgressInfo(getUid(), selectedDate, ::setProgress)


        binding.exOneCalendar.monthScrollListener = { month ->
            viewModel.setMonthHasTrain(getUid(), month.yearMonth.year, month.yearMonth.monthValue)
            updateTitle()
        }

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

    }

    private fun setProgress(progressData: ProgressData?) {
        if (progressData == null) {
            planStateView.isVisible = false
        } else {
            planStateView.isVisible = true
            planStateView.setPlanData(
                progressData.context.goal,
                progressData.completedCount,
                progressData.totalDays,
                progressData.status,
                progressData.totalDistances,
                progressData.totalTimes,
            )
        }
    }

    private fun initListener() = with(binding) {
        lyPlan.setOnClickListener { moveToPlanDetailFragment() }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.dateList.collectLatest { newDateList ->
                val prevDates = prevDateMap.keys
                val newDates = newDateList.keys
                val commonDates = prevDates.union(newDates)
                for (date in commonDates) {
                    monthCalendarView.notifyDateChanged(LocalDate.parse(date))
                }
                dateClicked(selectedDate)
                prevDateMap = newDateList
            }
        }
    }

    private fun moveToPlanDetailFragment() {
        val action = ScheduleFragmentDirections.actionScheduleFragmentToPlanDetailFragment2()
        findNavController().navigate(action)
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
                    viewModel.dateList.value.containsKey(data.date.toString())
                )
            }
        }
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
        viewModel.dateProgressInfo(getUid(), selectedDate, ::setProgress)
        viewModel.dateList.value.let { dateList ->
            flag = true
            for (trainDate in dateList.keys) {
                if (LocalDate.parse(trainDate) == selectedDate) {
                    dateList[trainDate]?.let { setResultView(selectedDate, it) }
                    flag = false
                    break
                }
            }
            if (flag)
                setResultView(selectedDate, emptyList())
        }
    }

    private fun setResultView(selectedDate: LocalDate, contentList: List<ContentListDto>) {
        val trainInfo = binding.tvInst
        trainInfo.isVisible = true
        if (contentList.isEmpty()) {
            trainInfo.setTitle(
                selectedDate.toString(),
                requireContext().getString(R.string.nothing)
            )
            binding.vpReport.isVisible = false
            binding.lyNothing.isVisible = true
        } else {
            trainInfo.setTitle(selectedDate.toString(), null)
            binding.lyNothing.isVisible = false
            binding.vpReport.isVisible = true
            adapter.updateItems(contentList)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle() {
        val month = monthCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.exOneYearText.text = month.year.toString()
        binding.exOneMonthText.text = month.month.displayText(short = true)
    }
}

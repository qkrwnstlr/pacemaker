package com.ssafy.presentation.planUI.planDetail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.ssafy.presentation.component.MiniDateContainer
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentPlanDetailBinding
import com.ssafy.presentation.planUI.registerPlan.ScheduleDialogFragment
import com.ssafy.presentation.utils.displayText
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class PlanDetailFragment : BaseFragment<FragmentPlanDetailBinding>(
    FragmentPlanDetailBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() = with(binding.basePlanLayout) {
        val daysOfWeek: List<DayOfWeek> = daysOfWeek()
        llLegend.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText(narrow = true)
            }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(0)
        val endMonth = currentMonth.plusMonths(1)
        setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
    }

    private fun initListener() = with(binding.basePlanLayout) {
        ivRight.setOnClickListener {
            cvDays.findFirstVisibleMonth()?.let {
                cvDays.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        ivLeft.setOnClickListener {
            cvDays.findFirstVisibleMonth()?.let {
                cvDays.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        fabGoBack.setOnClickListener {
            val action = PlanDetailFragmentDirections.actionPlanDetailFragmentToScheduleFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) = with(binding.basePlanLayout) {

        cvDays.dayBinder = object : MonthDayBinder<MiniDateContainer> {
            override fun create(view: View): MiniDateContainer =
                MiniDateContainer(view, ::dateClicked)

            override fun bind(container: MiniDateContainer, data: CalendarDay) {
                container.day = data
                if (data.position == DayPosition.MonthDate) {
                    container.binding.exOneDayText.text = data.date.dayOfMonth.toString()
                } else {
                    container.binding.root.background = null
                    container.binding.exThreeDotView.visibility = View.GONE
                }
            }
        }

        cvDays.monthScrollListener = { updateTitle() }
        cvDays.setup(startMonth, endMonth, daysOfWeek.first())
        cvDays.scrollToMonth(currentMonth)
    }

    private fun dateClicked(date: LocalDate) {
        val manager = requireActivity().supportFragmentManager
        ScheduleDialogFragment(date).show(manager, "ScheduleDialog")
    }

    private fun updateTitle() = with(binding.basePlanLayout) {
        val month = cvDays.findFirstVisibleMonth()?.yearMonth ?: return
        tvYear.text = month.year.toString()
        tvMonth.text = month.month.displayText(short = true)
    }
}
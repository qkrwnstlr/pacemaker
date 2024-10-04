package com.ssafy.presentation.planUI.planDetail

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.ssafy.domain.dto.plan.PlanInfo
import com.ssafy.presentation.component.MiniDateContainer
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentPlanDetailBinding
import com.ssafy.presentation.planUI.registerPlan.ScheduleDialogFragment
import com.ssafy.presentation.utils.displayText
import com.ssafy.presentation.utils.toLocalDate
import com.ssafy.presentation.utils.toMakeDurationDate
import com.ssafy.presentation.utils.toPaceString
import com.ssafy.presentation.utils.toTimeString
import com.ssafy.presentation.utils.toWeekString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Collections
import java.util.SortedSet

@AndroidEntryPoint
class PlanDetailFragment : BaseFragment<FragmentPlanDetailBinding>(
    FragmentPlanDetailBinding::inflate
) {
    private val viewModel: PlanDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        initCollect()
        viewModel.getPlanInfo(::showSnackBar)
    }

    private fun initView() = with(binding.basePlanLayout) {
        val daysOfWeek: List<DayOfWeek> = daysOfWeek()
        llLegend.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText(narrow = true)
            }

        setUpCalendar(Collections.emptySortedSet())
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.planInfo.collectLatest { info ->
                info?.let { setPlanInfo(it) }
            }
        }
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
            moveToHomeFragment()
        }
    }

    private suspend fun setPlanInfo(planInfo: PlanInfo) = withContext(Dispatchers.Main) {
        with(binding.basePlanLayout) {
            val dateSet = planInfo.planTrains.map { it.trainDate.toLocalDate() }.toSortedSet()

            tvTime.text = planInfo.totalTimes.toTimeString()
            tvPace.text = planInfo.planTrains.map { it.trainPace }.average().toPaceString()
            tvCount.text = planInfo.totalDays.toString()
            tvPlanPeriod.text = planInfo.createdAt.toMakeDurationDate(planInfo.expiredAt)
            tvPlanWeek.text = planInfo.context.trainDayOfWeek.toWeekString()
            tvPlanCount.text = planInfo.totalDays.toString()
            setUpCalendar(dateSet)
        }
    }

    private fun setUpCalendar(
        dateList: SortedSet<LocalDate>
    ) = with(binding.basePlanLayout.cvDays) {
        dayBinder = object : MonthDayBinder<MiniDateContainer> {
            override fun create(view: View): MiniDateContainer =
                MiniDateContainer(view, ::dateClicked)

            override fun bind(container: MiniDateContainer, data: CalendarDay) {
                container.day = data
                if (data.position == com.kizitonwose.calendar.core.DayPosition.MonthDate) {
                    container.binding.exOneDayText.text = data.date.dayOfMonth.toString()

                    if (dateList.contains(data.date)) return
                    container.binding.exThreeDotView.visibility = View.INVISIBLE
                    container.binding.root.isEnabled = false
                } else {
                    container.binding.root.background = null
                    container.binding.exThreeDotView.visibility = View.GONE
                }
            }
        }

        val currentDate = YearMonth.now()
        val firstMonth = dateList.firstOrNull()?.month ?: currentDate.month
        val lastMonth = dateList.lastOrNull()?.month ?: currentDate.month

        val firstYear = dateList.firstOrNull()?.year ?: currentDate.year
        val lastYear = dateList.lastOrNull()?.year ?: currentDate.year

        val startMonth = YearMonth.of(firstYear, firstMonth)
        val endMonth = YearMonth.of(lastYear, lastMonth)
        monthScrollListener = { updateTitle() }
        setup(startMonth, endMonth, daysOfWeek().first())
        scrollToMonth(currentDate)
    }

    private fun dateClicked(date: LocalDate) {
        val manager = requireActivity().supportFragmentManager
        val planTrain = viewModel.planInfo.value?.planTrains?.find {
            it.trainDate.toLocalDate() == date
        }

        ScheduleDialogFragment(date, planTrain).show(manager, "ScheduleDialog")
    }

    private fun updateTitle() = with(binding.basePlanLayout) {
        val month = cvDays.findFirstVisibleMonth()?.yearMonth ?: return
        tvYear.text = month.year.toString()
        tvMonth.text = month.month.displayText(short = true)
    }

    private fun moveToHomeFragment() {
        val action = PlanDetailFragmentDirections.actionPlanDetailFragmentToHomeFragment()
        findNavController().navigate(action)
    }

}
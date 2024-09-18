package com.ssafy.presentation.planUI.registerPlan

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.ssafy.presentation.component.MiniDateContainer
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRegisterPlanBinding
import com.ssafy.presentation.utils.displayText
import java.time.DayOfWeek
import java.time.YearMonth

class RegisterPlanFragment : BaseFragment<FragmentRegisterPlanBinding>(
    FragmentRegisterPlanBinding::inflate
) {
    private val viewModel: RegisterPlanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initTopSheet()
        initListener()
        showScheduleDialog()
    }

    private fun initView() = with(binding.chatUi) {
        val slideDown = AnimationUtils.loadAnimation(
            requireContext(),
            com.ssafy.presentation.R.anim.fade_slide_down
        )

        tvTitle.startAnimation(slideDown)
        tvLikeWeek.startAnimation(slideDown)
        clWeek.startAnimation(slideDown)
    }

    private fun initListener() = with(binding.chatUi) {
        ivSend.setOnClickListener {
            val text = etChat.text.toString()
            etChat.text = null
            showSnackStringBar(text)
        }
    }

    private fun initTopSheet() = with(binding.topSheetTrain) {
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
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) = with(binding.topSheetTrain.cvDays) {

        dayBinder = object : MonthDayBinder<MiniDateContainer> {
            override fun create(view: View): MiniDateContainer =
                MiniDateContainer(view)

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

        monthScrollListener = { updateTitle() }
        setup(startMonth, endMonth, daysOfWeek.first())
        scrollToMonth(currentMonth)
    }

    private fun updateTitle() = with(binding.topSheetTrain) {
        val month = cvDays.findFirstVisibleMonth()?.yearMonth ?: return
        tvYear.text = month.year.toString()
        tvMonth.text = month.month.displayText(short = true)
    }

    private fun showScheduleDialog() {
        val manager = requireActivity().supportFragmentManager
        ScheduleDialogFragment().show(manager, "ScheduleDialog")
    }

}

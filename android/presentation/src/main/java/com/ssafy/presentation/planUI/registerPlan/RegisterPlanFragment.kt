package com.ssafy.presentation.planUI.registerPlan

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.ssafy.presentation.component.MiniDateContainer
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRegisterPlanBinding
import com.ssafy.presentation.homeUI.TopSheetBehavior
import com.ssafy.presentation.utils.displayText
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@AndroidEntryPoint
class RegisterPlanFragment : BaseFragment<FragmentRegisterPlanBinding>(
    FragmentRegisterPlanBinding::inflate
) {
    private val viewModel: RegisterPlanViewModel by viewModels()
    private val behavior by lazy { TopSheetBehavior.from(binding.topSheetTrain.root) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initTopSheet()
        initListener()
    }

    private fun initView() = with(binding.chatUi) {
        val uid = getUid()
        val slideDown = AnimationUtils.loadAnimation(
            requireContext(),
            com.ssafy.presentation.R.anim.fade_slide_down
        )

        tvTitle.startAnimation(slideDown)
        viewModel.getCoach(uid)
    }

    private fun initListener() = with(binding) {
        chatUi.ivSend.setOnClickListener {
            val text = chatUi.etChat.text.toString()
            chatUi.etChat.text = null
            showSnackStringBar(text)
        }

        topSheetTrain.fabBlue.setOnClickListener {
            val action =
                RegisterPlanFragmentDirections.actionRegisterPlanFragmentToPlanDetailFragment()
            findNavController().navigate(action)
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

        monthScrollListener = { updateTitle() }
        setup(startMonth, endMonth, daysOfWeek.first())
        scrollToMonth(currentMonth)
    }

    private fun updateTitle() = with(binding.topSheetTrain) {
        val month = cvDays.findFirstVisibleMonth()?.yearMonth ?: return
        tvYear.text = month.year.toString()
        tvMonth.text = month.month.displayText(short = true)
    }

    private fun dateClicked(date: LocalDate) {
        val manager = requireActivity().supportFragmentManager
        ScheduleDialogFragment(date).show(manager, "ScheduleDialog")
    }

    private fun scrollUpCalender() {
        behavior.state = TopSheetBehavior.STATE_COLLAPSED
    }

    private fun scrollDownCalendar() {
        behavior.state = TopSheetBehavior.STATE_EXPANDED
    }

}

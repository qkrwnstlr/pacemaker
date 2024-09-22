package com.ssafy.presentation.planUI.registerPlan

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
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
import com.ssafy.presentation.planUI.registerPlan.adapter.ChatAdapter
import com.ssafy.presentation.utils.displayText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@AndroidEntryPoint
class RegisterPlanFragment : BaseFragment<FragmentRegisterPlanBinding>(
    FragmentRegisterPlanBinding::inflate
) {
    private val viewModel: RegisterPlanViewModel by viewModels()
    private val behavior by lazy { TopSheetBehavior.from(binding.topSheetTrain.root) }
    private val adapter by lazy {
        ChatAdapter().apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    binding.chatUi.rvPlanChat.scrollToPosition(positionStart)
                    super.onItemRangeInserted(positionStart, itemCount)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initTopSheet()
        initListener()
        initCollect()
    }

    private fun initView() = with(binding.chatUi) {
        val uid = getUid()
        val slideDown = AnimationUtils.loadAnimation(
            requireContext(),
            com.ssafy.presentation.R.anim.fade_slide_down
        )

        tvTitle.startAnimation(slideDown)
        viewModel.getCoach(uid)
        rvPlanChat.adapter = adapter
    }

    private fun initListener() = with(binding) {
        chatUi.ivSend.setOnClickListener {
            val text = chatUi.etChat.text.toString()
            chatUi.etChat.text = null
            viewModel.sendMyMessage(text)
        }

        topSheetTrain.fabBlue.setOnClickListener {
            val action =
                RegisterPlanFragmentDirections.actionRegisterPlanFragmentToPlanDetailFragment()
            findNavController().navigate(action)
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.planData.collectLatest { chatList ->
                adapter.submitList(chatList)
            }
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

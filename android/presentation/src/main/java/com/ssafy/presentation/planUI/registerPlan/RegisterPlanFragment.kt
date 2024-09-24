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
import com.ssafy.presentation.utils.toAgeString
import com.ssafy.presentation.utils.toEmptyOrHeight
import com.ssafy.presentation.utils.toEmptyOrWeight
import com.ssafy.presentation.utils.toGenderString
import com.ssafy.presentation.utils.toInjuries
import com.ssafy.presentation.utils.toLocalDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.SortedSet

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
                    val lastIndex = positionStart + itemCount - 1
                    binding.chatUi.rvPlanChat.scrollToPosition(lastIndex)
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
        viewModel.getCoach(uid, ::setSendClickable)
        rvPlanChat.adapter = adapter
    }

    private fun setSendClickable(able: Boolean) = with(binding.chatUi) {
        ivSend.isClickable = able
    }

    private fun initListener() = with(binding) {
        chatUi.ivSend.setOnClickListener {
            val text = chatUi.etChat.text.toString()
            if (text.isBlank()) return@setOnClickListener
            chatUi.etChat.text = null
            viewModel.sendMyMessage(text, ::makeFailMessage)
        }
        setSendClickable(false)

        topSheetTrain.fabBlue.setOnClickListener {
            val action =
                RegisterPlanFragmentDirections.actionRegisterPlanFragmentToPlanDetailFragment()
            findNavController().navigate(action)
        }

        chatUi.tvTitle.setOnClickListener {
            chatUi.userInfo.root.apply {
                val visible = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
                visibility = visible
            }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectChatList()
            collectContext()
            collectPlan()
        }
    }

    private fun CoroutineScope.collectChatList() = launch {
        viewModel.chatData.collectLatest { chatList ->
            adapter.submitList(chatList)
        }
    }

    private fun CoroutineScope.collectContext() = launch {
        viewModel.contextData.collectLatest { context ->
            val userInfo = context.userInfo
            binding.chatUi.userInfo.apply {
                tvAge.text = userInfo.age.toAgeString()
                tvGender.text = userInfo.gender.toGenderString()
                tvHeight.text = userInfo.height.toEmptyOrHeight()
                tvWeight.text = userInfo.weight.toEmptyOrWeight()
                tvInjuries.text = userInfo.injuries.toInjuries()
            }
        }
    }

    private fun CoroutineScope.collectPlan() = launch {
        viewModel.planData.collectLatest { plan ->
            val dateList = plan.planTrains.map { it.trainDate.toLocalDate() }.toSortedSet()
            setUpCalendar(dateList)
            binding.topSheetTrain.fabBlue.isEnabled = !dateList.isEmpty()

            if (dateList.isEmpty()) scrollUpCalender()
            else scrollDownCalendar()
        }
    }

    private fun initTopSheet() = with(binding.topSheetTrain) {
        val daysOfWeek: List<DayOfWeek> = daysOfWeek()
        llLegend.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText(narrow = true)
            }

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

    private fun setUpCalendar(
        dateList: SortedSet<LocalDate>
    ) = with(binding.topSheetTrain.cvDays) {
        dayBinder = object : MonthDayBinder<MiniDateContainer> {
            override fun create(view: View): MiniDateContainer =
                MiniDateContainer(view, ::dateClicked)

            override fun bind(container: MiniDateContainer, data: CalendarDay) {
                container.day = data
                if (data.position == DayPosition.MonthDate) {
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

        val startMonth = YearMonth.of(currentDate.year, firstMonth)
        val endMonth = YearMonth.of(currentDate.year, lastMonth)
        monthScrollListener = { updateTitle() }
        setup(startMonth, endMonth, daysOfWeek().first())
        scrollToMonth(currentDate)
    }

    private fun updateTitle() = with(binding.topSheetTrain) {
        val month = cvDays.findFirstVisibleMonth()?.yearMonth ?: return
        tvYear.text = month.year.toString()
        tvMonth.text = month.month.displayText(short = true)
    }

    private fun dateClicked(date: LocalDate) {
        val manager = requireActivity().supportFragmentManager
        val planTrain = viewModel.planData.value.planTrains.find {
            it.trainDate.toLocalDate() == date
        }

        println(planTrain)
        ScheduleDialogFragment(date, planTrain).show(manager, "ScheduleDialog")
    }

    private suspend fun makeFailMessage(message: String) = withContext(Dispatchers.Main) {
        showSnackStringBar(message)
    }

    private fun scrollUpCalender() {
        behavior.state = TopSheetBehavior.STATE_COLLAPSED
    }

    private fun scrollDownCalendar() {
        behavior.state = TopSheetBehavior.STATE_EXPANDED
    }

}

package com.ssafy.presentation.planUI.registerPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.DialogSelectWeekBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY

class SelectWeekDialog(
    private val trainDays: List<String>,
    private val setPlanDate: (Set<DayOfWeek>) -> Unit
) : DialogFragment() {
    private var _binding: DialogSelectWeekBinding? = null
    private val binding: DialogSelectWeekBinding get() = _binding!!
    private val viewModel: SelectWeekViewModel by viewModels()
    private val dayButtons: Map<DayOfWeek, ToggleButton> by lazy {
        mapOf(
            MONDAY to binding.tbMon,
            TUESDAY to binding.tbTue,
            WEDNESDAY to binding.tbWed,
            THURSDAY to binding.tbThu,
            FRIDAY to binding.tbFri,
            SATURDAY to binding.tbSat,
            SUNDAY to binding.tbSun
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectWeekBinding.inflate(inflater, container, false)

        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_background_white)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initCollect()
        viewModel.addDays(trainDays)
    }

    private fun initListener() = with(binding) {
        btnOk.setOnClickListener {
            setPlanDate(viewModel.weekListState.value)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        tbMon.setOnCheckedChangeListener { _, isSelected ->
            viewModel.selectDay(MONDAY, isSelected, ::isNotAvailable)
        }

        tbTue.setOnCheckedChangeListener { _, isSelected ->
            viewModel.selectDay(TUESDAY, isSelected, ::isNotAvailable)
        }

        tbWed.setOnCheckedChangeListener { _, isSelected ->
            viewModel.selectDay(WEDNESDAY, isSelected, ::isNotAvailable)
        }

        tbThu.setOnCheckedChangeListener { _, isSelected ->
            viewModel.selectDay(THURSDAY, isSelected, ::isNotAvailable)
        }

        tbFri.setOnCheckedChangeListener { _, isSelected ->
            viewModel.selectDay(FRIDAY, isSelected, ::isNotAvailable)
        }

        tbSat.setOnCheckedChangeListener { _, isSelected ->
            viewModel.selectDay(SATURDAY, isSelected, ::isNotAvailable)
        }

        tbSun.setOnCheckedChangeListener { _, isSelected ->
            viewModel.selectDay(SUNDAY, isSelected, ::isNotAvailable)
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.weekListState.collectLatest { weekList: Set<DayOfWeek> ->
                binding.btnOk.isEnabled = weekList.isNotEmpty()

                DayOfWeek.entries.forEach { day ->
                    dayButtons[day]?.isChecked = weekList.contains(day)
                }
            }
        }
    }

    private fun isNotAvailable(day: DayOfWeek) {
        dayButtons[day]?.isChecked = false
        Toast.makeText(requireContext(), IS_NOT_VALID, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val IS_NOT_VALID = "연속적인 러닝은 부상을 야기합니다."
    }
}
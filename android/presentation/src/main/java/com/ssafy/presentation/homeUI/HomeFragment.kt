package com.ssafy.presentation.homeUI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
  OnMapReadyCallback {
  private lateinit var topSheetBehavior: TopSheetBehavior<ConstraintLayout>
  private lateinit var topSheetLayout: ConstraintLayout
  private lateinit var topSheetBodyLayout: LinearLayout

  private lateinit var weekCalendarView: WeekCalendarView

  private val viewModel: HomeViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val mapFragment = SupportMapFragment.newInstance()
    getParentFragmentManager()
      .beginTransaction()
      .add(R.id.map, mapFragment)
      .commit()
    mapFragment.getMapAsync(this)

    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onMapReady(map: GoogleMap) {
    val point = LatLng(37.514655, 126.979974)
    map.addMarker(MarkerOptions().position(point).title("현위치"))
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 12f))
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    topSheetLayout = requireView().findViewById(R.id.top_sheet_layout)

    topSheetBehavior = TopSheetBehavior.from(topSheetLayout).apply {
      setHideable(false)
      setHalfable(true)
      setHalfHeight(1500)
    }

    topSheetBodyLayout = topSheetLayout.findViewById<LinearLayout?>(R.id.top_sheet_body).apply {
      visibility = View.GONE
    }

    topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
      override fun onStateChanged(topSheet: View, newState: Int) {
        when (newState) {
          TopSheetBehavior.STATE_COLLAPSED -> {
            topSheetBodyLayout.visibility = View.GONE
          }

          TopSheetBehavior.STATE_EXPANDED -> {
            val action = HomeFragmentDirections.actionHomeFragmentToScheduleFragment()
            findNavController().navigate(action)
          }

          else -> {
            topSheetBodyLayout.visibility = View.VISIBLE
          }
        }
      }

      override fun onSlide(topSheet: View, slideOffset: Float) {
        if (slideOffset != 0f) {
          topSheetBodyLayout.apply {
            layoutParams.height = ((topSheet.height - topSheetBehavior.peekHeight) * slideOffset).toInt()
            requestLayout()
          }
        }
      }
    })

    initWeekCalendar()

    initCollect()
  }

  private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      collectTrainingState()
    }
  }

  private fun CoroutineScope.collectTrainingState() = launch {
    viewModel.trainingState.collect { training ->
      topSheetBodyLayout.apply {
        removeAllViews()
        // TODO : 상태에 맞는 적절한 view로 수정
        val textView = TextView(context).apply {
          text = if (training) "Is Finished" else "Is not Finished"
          textSize = 18f
          setPadding(16, 16, 16, 16)
        }
        addView(textView)
      }
    }
  }

  private fun initWeekCalendar() {
    val today = LocalDate.now()
    val currentMonth = YearMonth.now()
    val startMonth = currentMonth.minusMonths(1)
    val endMonth = currentMonth.plusMonths(1)
    val daysOfWeek = daysOfWeek()

    weekCalendarView = topSheetLayout.findViewById(R.id.week_calendar)

    weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
      override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view)
      override fun bind(container: WeekDayViewContainer, data: WeekDay) {
        container.apply {
          day = data
          textView.text = data.date.dayOfMonth.toString()
          if (data.date == today) container.ly.setBackgroundResource(R.drawable.day_selected_bg)
          setOnClickListener { topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED }
        }
      }
    }

    weekCalendarView.setup(
      startMonth.atStartOfMonth(),
      endMonth.atEndOfMonth(),
      daysOfWeek.first(),
    )

    weekCalendarView.scrollToWeek(LocalDate.now())
  }
}
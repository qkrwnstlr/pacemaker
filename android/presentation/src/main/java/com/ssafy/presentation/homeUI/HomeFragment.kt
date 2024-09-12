package com.ssafy.presentation.homeUI

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
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
import com.ssafy.presentation.component.CoachCreateButton
import com.ssafy.presentation.component.TrainInfoChartView
import com.ssafy.presentation.component.TrainRestMessageView
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import com.ssafy.presentation.planUI.startPlan.StartPlanFragmentDirections
import com.ssafy.presentation.scheduleUI.schedule.TrainResultView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
            layoutParams.height =
              ((topSheet.height - topSheetBehavior.peekHeight) * slideOffset).toInt()
            requestLayout()
          }
        }
      }
    })

    initView()
    initCollect()
  }

  private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      collectTrainingState()
    }
  }

  private fun CoroutineScope.collectTrainingState() = launch {
    viewModel.trainingState.collect {
      topSheetBodyLayout.apply {
        removeAllViews()
        val trainInfoView = when (it) {
          1 -> TrainInfoChartView(context).also {
            makeChart(it.findViewById(R.id.barChart))
            topSheetBehavior.setHalfHeight(700)
          }

          2 -> TrainResultView(context).also {
            setPieChart(it.findViewById(R.id.chart_pace), 75f)
            setPieChart(it.findViewById(R.id.chart_heart), 60f)
            setPieChart(it.findViewById(R.id.chart_step), 70f)
            topSheetBehavior.setHalfHeight(900)
          }

          3 -> TrainRestMessageView(context).also {
            topSheetBehavior.setHalfHeight(250)
          }

          4 -> CoachCreateButton(context).also {
            when (viewModel.coachState.value) {
              1 -> R.drawable.coach_mike_white
              2 -> R.drawable.coach_jamie_white
              3 -> R.drawable.coach_danny_white
              else -> return@also
            }.run {
              it.setIconResource(this)
            }

            it.setOnClickListener {
              val action = HomeFragmentDirections.actionHomeFragmentToStartPlanFragment()
              findNavController().navigate(action)
            }

            topSheetBehavior.setHalfHeight(350)
          }

          else -> return@collect
        }
        addView(trainInfoView)
      }
    }
  }

  private fun initView() {
    initWeekCalendar()
    initButton()
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

  private fun initButton() {
    binding.startRunButton.setOnClickListener {
      val action = HomeFragmentDirections.actionHomeFragmentToRunningInfoFragment()
      findNavController().navigate(action)
    }

    with(binding.profileButton) {
      setOnClickListener {
        val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
        findNavController().navigate(action)
      }

      when (viewModel.coachState.value) {
        1 -> R.drawable.coach_mike
        2 -> R.drawable.coach_jamie
        3 -> R.drawable.coach_danny
        else -> return@with
      }.run {
        setImageResource(this)
      }
    }

    binding.planButton.setOnClickListener {
      val action = HomeFragmentDirections.actionHomeFragmentToScheduleFragment()
      findNavController().navigate(action)
    }
  }

  private fun makeChart(barChart: BarChart) {
    val entries = ArrayList<BarEntry>()
    entries.add(BarEntry(2.5f, 0.2f))
    entries.add(BarEntry(3.5f, 1.6f))
    entries.add(BarEntry(4.5f, 1.2f))
    entries.add(BarEntry(5.5f, 1.6f))
    entries.add(BarEntry(6.5f, 1.2f))
    entries.add(BarEntry(7.5f, 1.6f))
    entries.add(BarEntry(8.5f, 0.2f))

    barChart.apply {
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
    }
    val set = BarDataSet(entries, "DataSet")

    val colors = List(entries.size) { index ->
      if (index % 2 == 0) R.color.thirdPrimary else R.color.secondPrimary
    }
    set.setColors(colors.toIntArray(), barChart.context)

    val dataSet: ArrayList<IBarDataSet> = ArrayList()
    dataSet.add(set)
    val data = BarData(dataSet)
    data.barWidth = 0.8f //막대 너비 설정
    barChart.apply {
      this.data = data //차트의 데이터를 data로 설정해줌.
      setFitBars(true)
      invalidate()
    }
  }

  private fun setPieChart(chart: PieChart, value: Float) {
    val maxValue = 100f

    val entry = ArrayList<PieEntry>().apply {
      add(PieEntry(maxValue - value))
      add(PieEntry(value, ""))
    }

    val dataSet = PieDataSet(entry, "").apply {
      colors = listOf(
        Color.parseColor("#FFFFFFFF"),
        Color.parseColor("#5973FF"),
      )
      setDrawValues(false)
    }

    chart.apply {
      data = PieData(dataSet)
      holeRadius = 75f
      transparentCircleRadius = 75f
      setDrawCenterText(false)
      legend.isEnabled = false
      description.isEnabled = false
      invalidate()
    }
  }
}
package com.ssafy.presentation.homeUI

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.WeekDayBinder
import com.ssafy.presentation.R
import com.ssafy.presentation.component.CreatePlanButton
import com.ssafy.presentation.component.TrainInfoChartView
import com.ssafy.presentation.component.TrainRestMessageView
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import com.ssafy.presentation.homeUI.TopSheetBehavior.TopSheetCallback
import com.ssafy.presentation.scheduleUI.schedule.TrainResultView
import com.ssafy.presentation.utils.toContentString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: HomeViewModel by viewModels()
    private var myLocationListener: LocationListener? = null
    private lateinit var behavior: TopSheetBehavior<ConstraintLayout>
    private val currentMonth = YearMonth.now()
    private val startMonth = currentMonth.minusMonths(1)
    private val endMonth = currentMonth.plusMonths(1)
    private val daysOfWeek = daysOfWeek()
    private val weekCalendarView: WeekCalendarView get() = binding.topSheet.weekCalendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initMapView()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initMapView() {
        val mapFragment = SupportMapFragment.newInstance()
        getParentFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        val point = LatLng(37.514655, 126.979974)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 12f))

        getMyLocation(map)
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(map: GoogleMap) {
        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        myLocationListener = LocationListener { location -> setMyLocation(map, location) }.apply {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
            }

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f,
                    this
                )
            }
        }

    }

    private fun setMyLocation(map: GoogleMap, location: Location) {
        if (myLocationListener != null) {
            val locationManager =
                requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(myLocationListener!!)
            myLocationListener = null
        }

        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        map.animateCamera(cameraUpdate)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker)
        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, MARKER_WIDTH, MARKER_HEIGHT, false)
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeBitmap)

        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(bitmapDescriptor)

        map.addMarker(markerOptions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTermMonthHasTrain(
            getUid(),
            startMonth.year,
            startMonth.monthValue,
            currentMonth.year,
            currentMonth.monthValue,
            endMonth.year,
            endMonth.monthValue
        )

        initView()
        initListener()
        initCollect()

        viewModel.setDate(LocalDate.now())
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectTrainingState()
            collectSelectDate()
            collectDateListState()
        }
    }

    private fun CoroutineScope.collectDateListState() = launch {
        viewModel.dateList.collect { newDateList ->
            val newDates = newDateList.keys
            for (date in newDates) {
                weekCalendarView.notifyDateChanged(LocalDate.parse(date))
            }
        }
    }

    private fun CoroutineScope.collectTrainingState() = launch {
        viewModel.report.collect {
            val bodyLayout = binding.topSheet.topSheetBody
            bodyLayout.removeAllViews()

            val trainView = when (viewModel.trainingState.value) {
                TRAIN_INFO -> {
                    val trainInfoView =
                        TrainInfoChartView(requireContext())// TrainInfoView(requireContext())
                    it.planTrain?.let { plan ->
                        trainInfoView.makeEntryChart(plan)
                    }
                    trainInfoView
                }

                TRAIN_RESULT -> {
                    val trainResultView = TrainResultView(requireContext())
                    it.trainReport?.let { report ->
                        trainResultView.setResultData(report)
                    }
                    trainResultView
                }

                TRAIN_REST_MESSAGE -> TrainRestMessageView(requireContext())
                CREATE_PLAN -> CreatePlanButton(requireContext()).also { setTopSheetHalfExpand() }
                else -> return@collect
            }

            bodyLayout.addView(trainView)
            bodyLayout.doOnNextLayout {
                if (bodyLayout.measuredHeight != 0) {
                    behavior.setHalfHeight(bodyLayout.measuredHeight)
                    behavior.state = TopSheetBehavior.STATE_HALF_EXPANDED
                }
            }

            when (viewModel.trainingState.value) {
                TRAIN_INFO -> {
                    binding.topSheet.trainInfoTitle.tvResultTitle.text = "오늘의 훈련 목표"
                    binding.topSheet.trainInfoTitle.tvPlanInst.isVisible = true
                    binding.topSheet.trainInfoTitle.tvPlanInst.text =
                        it.planTrain.toContentString()
                }

                TRAIN_RESULT -> {
                    binding.topSheet.trainInfoTitle.tvResultTitle.text = "오늘의 훈련 목표"
                    binding.topSheet.trainInfoTitle.tvPlanInst.isVisible = true
                    binding.topSheet.trainInfoTitle.tvPlanInst.text =
                        it.planTrain.toContentString()
                }

                TRAIN_REST_MESSAGE -> {
                    binding.topSheet.trainInfoTitle.tvResultTitle.text = "오늘은 훈련이 없어요"
                    binding.topSheet.trainInfoTitle.tvPlanInst.isVisible = true
                    binding.topSheet.trainInfoTitle.tvPlanInst.text = "잘 쉬는 것도 훈련입니다."
                }

                CREATE_PLAN -> {
                    binding.topSheet.trainInfoTitle.tvResultTitle.text = "진행중인 훈련이 없어요"
                    binding.topSheet.trainInfoTitle.tvPlanInst.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun CoroutineScope.collectSelectDate() = launch {
        var prevDate = viewModel.selectDate.value
        viewModel.selectDate.collectLatest { date ->
            binding.topSheet.weekCalendar.notifyWeekChanged(date)
            binding.topSheet.weekCalendar.notifyWeekChanged(prevDate)
            prevDate = date

            val bodyLayout = binding.topSheet.topSheetBody
            bodyLayout.removeAllViews()

            val trainView = when (viewModel.trainingState.value) {
                TRAIN_INFO -> {
                    val trainInfoView =
                        TrainInfoChartView(requireContext())// TrainInfoView(requireContext())
                    viewModel.report.value.planTrain?.let { plan ->
                        trainInfoView.makeEntryChart(plan)
                    }
                    trainInfoView
                }

                TRAIN_RESULT -> {
                    val trainResultView = TrainResultView(requireContext())
                    viewModel.report.value.trainReport?.let { report ->
                        trainResultView.setResultData(report)
                    }
                    trainResultView
                }

                TRAIN_REST_MESSAGE -> TrainRestMessageView(requireContext())
                CREATE_PLAN -> CreatePlanButton(requireContext()).also { setTopSheetHalfExpand() }
                else -> return@collectLatest
            }

            bodyLayout.addView(trainView)
            bodyLayout.doOnNextLayout {
                if (bodyLayout.measuredHeight != 0) {
                    behavior.setHalfHeight(bodyLayout.measuredHeight)
                    behavior.state = TopSheetBehavior.STATE_HALF_EXPANDED
                }
            }

            when (viewModel.trainingState.value) {
                TRAIN_INFO -> {
                    binding.topSheet.trainInfoTitle.tvResultTitle.text = "오늘의 훈련 목표"
                    binding.topSheet.trainInfoTitle.tvPlanInst.isVisible = true
                    binding.topSheet.trainInfoTitle.tvPlanInst.text =
                        viewModel.report.value.planTrain.toContentString()
                }

                TRAIN_RESULT -> {
                    binding.topSheet.trainInfoTitle.tvResultTitle.text = "오늘의 훈련 목표"
                    binding.topSheet.trainInfoTitle.tvPlanInst.isVisible = true
                    binding.topSheet.trainInfoTitle.tvPlanInst.text =
                        viewModel.report.value.planTrain.toContentString()
                }

                TRAIN_REST_MESSAGE -> {
                    binding.topSheet.trainInfoTitle.tvResultTitle.text = "오늘은 훈련이 없어요"
                    binding.topSheet.trainInfoTitle.tvPlanInst.isVisible = true
                    binding.topSheet.trainInfoTitle.tvPlanInst.text = "잘 쉬는 것도 훈련입니다."
                }

                CREATE_PLAN -> {
                    binding.topSheet.trainInfoTitle.tvResultTitle.text = "진행중인 훈련이 없어요"
                    binding.topSheet.trainInfoTitle.tvPlanInst.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initView() = with(binding) {
        initWeekCalendar()
        initTopSheet()
        viewLifecycleOwner.lifecycleScope.launch {
            val imgUrl = viewModel.profileUrlFlow().firstOrNull()
            Glide.with(root).load(imgUrl).circleCrop().into(profileButton)
        }
    }

    private fun initTopSheet() = with(binding.topSheet) {
        behavior = TopSheetBehavior.from(root).apply {
            setHalfable(true)
            setTopSheetCallback(object : TopSheetCallback() {
                override fun onStateChanged(topSheet: View, newState: Int) {
                    when (newState) {
                        TopSheetBehavior.STATE_COLLAPSED -> topSheetBody.visibility = View.GONE
                        else -> topSheetBody.visibility = View.VISIBLE
                    }
                }

                override fun onSlide(topSheet: View, slideOffset: Float) {
                    if (slideOffset == 0f) return

                    val height = ((topSheet.height - peekHeight) * slideOffset).toInt()
                    topSheetBody.layoutParams.height = height
                    topSheetBody.requestLayout()
                }
            })
        }
    }

    private fun initWeekCalendar() {
        weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
            override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.apply {
                    day = data
                    textView.text = data.date.dayOfMonth.toString()
                    setOnClickListener {
                        viewModel.setDate(data.date)
                    }

                    val selectDate = viewModel.selectDate.value
                    if (data.date == selectDate) container.ly.setBackgroundResource(R.drawable.day_selected_bg)
                    else container.ly.setBackgroundColor(Color.TRANSPARENT)
                    hasTrain.visibility =
                        if (viewModel.dateList.value.containsKey(data.date.toString())) View.VISIBLE else View.INVISIBLE
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

    private fun initListener() {
        binding.startRunButton.setOnClickListener {
            viewModel.startExercise()
            val action = HomeFragmentDirections.actionHomeFragmentToRunningFragment()
            findNavController().navigate(action)
        }

        binding.profileButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            findNavController().navigate(action)
        }

        binding.planButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToScheduleFragment()
            findNavController().navigate(action)
        }
    }

    private fun setTopSheetHalfExpand() {
        behavior.state = TopSheetBehavior.STATE_HALF_EXPANDED
    }

    companion object {
        const val MARKER_WIDTH = 200
        const val MARKER_HEIGHT = 200

        const val TRAIN_INFO = 1
        const val TRAIN_RESULT = 2
        const val TRAIN_REST_MESSAGE = 3
        const val CREATE_PLAN = 4
    }
}
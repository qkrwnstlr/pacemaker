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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.WeekDayBinder
import com.ssafy.domain.dto.plan.PlanTrain
import com.ssafy.domain.dto.reports.Report
import com.ssafy.domain.dto.reports.TrainReport
import com.ssafy.presentation.R
import com.ssafy.presentation.component.CreatePlanButton
import com.ssafy.presentation.component.TrainInfoChartView
import com.ssafy.presentation.component.TrainRestMessageView
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import com.ssafy.presentation.homeUI.HomeViewModel.Companion.CREATE_PLAN
import com.ssafy.presentation.homeUI.HomeViewModel.Companion.TRAIN_INFO
import com.ssafy.presentation.homeUI.HomeViewModel.Companion.TRAIN_REST_MESSAGE
import com.ssafy.presentation.homeUI.HomeViewModel.Companion.TRAIN_RESULT
import com.ssafy.presentation.homeUI.TopSheetBehavior.STATE_COLLAPSED
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
    private lateinit var behavior: TopSheetBehavior<ConstraintLayout>
    private var myLocationListener: LocationListener? = null
    private var recentMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initMapView()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initMapView() {

        val existingFragment =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        val mapFragment = existingFragment ?: SupportMapFragment.newInstance().also {
            childFragmentManager.beginTransaction().replace(R.id.map, it).commit()
        }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        viewLifecycleOwner.lifecycleScope.launch {
            val latitude = viewModel.getLatitude()
            val longitude = viewModel.getLongitude()
            val point = LatLng(latitude, longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 17f))
            getMyLocation(map)
        }
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

        myLocationListener?.let {
            val locationManager =
                requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(it)
            myLocationListener = null
        }

        val latLng = LatLng(location.latitude, location.longitude)
        lifecycleScope.launch {
            viewModel.setLocation(location.latitude, location.longitude)
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        map.animateCamera(cameraUpdate)

        recentMarker?.remove()
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker)
        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, MARKER_WIDTH, MARKER_HEIGHT, false)
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeBitmap)

        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(bitmapDescriptor)
        recentMarker = map.addMarker(markerOptions)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        initCollect()
        viewModel.getCoach()
        viewModel.getTrain()
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
            newDateList.keys.forEach { date ->
                binding.topSheet.weekCalendar.notifyDateChanged(LocalDate.parse(date))
            }
        }
    }

    private fun CoroutineScope.collectTrainingState() = launch {
        viewModel.report.collectLatest {
            val bodyLayout = binding.topSheet.topSheetBody
            bodyLayout.removeAllViews()

            setTrainInfo(it)

            val trainView = when (it.trainingState) {
                TRAIN_INFO -> makeTrainInfoChartView(it.planTrain)
                TRAIN_RESULT -> makeTrainResultView(it.trainReport)
                TRAIN_REST_MESSAGE -> makeTrainRestMessageView()
                CREATE_PLAN -> makeCreatePlanButtonView()
                else -> return@collectLatest
            }.apply {
                if (it.trainingState != CREATE_PLAN) return@apply
                setOnClickListener { moveToPlanFragment() }
            }

            bodyLayout.addView(trainView)
        }
    }

    private fun CoroutineScope.collectSelectDate() = launch {
        var prevDate = viewModel.selectDate.value
        viewModel.selectDate.collectLatest { date ->
            binding.topSheet.weekCalendar.notifyWeekChanged(date)
            binding.topSheet.weekCalendar.notifyWeekChanged(prevDate)
            prevDate = date
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
            setHalfable(false)
            setTopSheetCallback(object : TopSheetCallback() {
                override fun onStateChanged(topSheet: View, newState: Int) {
                    when (newState) {
                        STATE_COLLAPSED -> topSheetBody.visibility = View.GONE
                        else -> topSheetBody.visibility = View.VISIBLE
                    }
                }

                override fun onSlide(topSheet: View, slideOffset: Float) {}
            })
        }
    }

    private fun initWeekCalendar() = with(binding.topSheet.weekCalendar) {
        dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
            override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.apply {
                    val selectDate = viewModel.selectDate.value
                    val trainDates = viewModel.dateList.value.keys
                    val bindDate = data.date

                    day = data
                    textView.text = bindDate.dayOfMonth.toString()
                    setOnClickListener { viewModel.setDate(bindDate) }

                    if (bindDate == selectDate) container.ly.setBackgroundResource(R.drawable.day_selected_bg)
                    else container.ly.setBackgroundColor(Color.TRANSPARENT)

                    if (trainDates.contains(bindDate.toString())) {
                        hasTrain.visibility = View.VISIBLE
                    } else {
                        hasTrain.visibility = View.INVISIBLE
                    }
                }
            }
        }


        val now = YearMonth.now()
        setup(
            startDate = now.minusMonths(1).atStartOfMonth(),
            endDate = now.plusMonths(1).atStartOfMonth(),
            firstDayOfWeek = daysOfWeek().first()
        )

        scrollToWeek(LocalDate.now())
    }

    private fun initListener() {
        binding.startRunButton.setOnClickListener {
            viewModel.startExercise()
            // TODO : show 3, 2, 1 or spinner
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

    private fun makeTrainInfoChartView(planTrain: PlanTrain?): TrainInfoChartView =
        TrainInfoChartView(requireContext()).apply {
            planTrain?.let { makeEntryChart(it) }
        }

    private fun makeTrainResultView(trainReport: TrainReport?): TrainResultView =
        TrainResultView(requireContext()).apply {
            trainReport?.let { setResultData(it) }
        }

    private fun makeTrainRestMessageView(): TrainRestMessageView =
        TrainRestMessageView(requireContext())

    private fun makeCreatePlanButtonView(): CreatePlanButton =
        CreatePlanButton(requireContext()).apply {
            val coachImgId = when (viewModel.coachState.value) {
                1 -> R.drawable.coach_mike_white
                0, 2 -> R.drawable.coach_jamie_white
                3 -> R.drawable.coach_danny_white
                else -> return@apply
            }
            setIconResource(coachImgId)
        }

    private fun setTrainInfo(report: Report) = with(binding.topSheet.trainInfoTitle) {
        when (report.trainingState) {
            TRAIN_INFO, TRAIN_RESULT -> {
                tvResultTitle.text = report.date.toTitleString()
                tvPlanInst.isVisible = true
                tvPlanInst.text = report.planTrain.toContentString()
            }

            TRAIN_REST_MESSAGE -> {
                tvResultTitle.text = NO_TRAIN
                tvPlanInst.isVisible = true
                tvPlanInst.text = REST_IS_GOOD
            }

            CREATE_PLAN -> {
                tvResultTitle.text = NO_PLAN
                tvPlanInst.visibility = View.INVISIBLE
            }

            else -> {}
        }
        setPeekHeight()
    }

    private fun setPeekHeight() = with(binding.topSheet) {
        val bodyHeight = if (topSheetBody.visibility == View.GONE) 0 else topSheetBody.height
        val height = root.height - bodyHeight - 9
        if (height > 0) behavior.peekHeight = height
    }

    private fun moveToPlanFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToStartPlanFragment()
        findNavController().navigate(action)
    }

    private fun LocalDate.toTitleString(): String {
        val today = LocalDate.now()
        return if (this == today) "오늘의 훈련 목표"
        else "${monthValue}월 ${dayOfMonth}일의 훈련 목표"
    }

    companion object {
        const val MARKER_WIDTH = 200
        const val MARKER_HEIGHT = 200

        const val NO_TRAIN = "훈련이 없는 날이에요"
        const val NO_PLAN = "진행중인 훈련이 없어요"
        const val REST_IS_GOOD = "잘 쉬는 것도 훈련입니다."
    }
}
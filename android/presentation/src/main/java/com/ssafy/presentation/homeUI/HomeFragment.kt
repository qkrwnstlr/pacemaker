package com.ssafy.presentation.homeUI

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnNextLayout
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
import com.kizitonwose.calendar.view.WeekDayBinder
import com.ssafy.presentation.R
import com.ssafy.presentation.component.CreatePlanButton
import com.ssafy.presentation.component.TrainInfoChartView
import com.ssafy.presentation.component.TrainRestMessageView
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import com.ssafy.presentation.homeUI.TopSheetBehavior.TopSheetCallback
import com.ssafy.presentation.scheduleUI.schedule.TrainResultView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
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

        initView()
        initListener()
        initCollect()
        // TODO 추후에 리포트 API가 나오면 변경됩니다. 현재는 진행중인 플랜의 유무만 확인합니다.
        viewModel.getPlanInfo()
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectTrainingState()
        }
    }

    private fun CoroutineScope.collectTrainingState() = launch {
        viewModel.trainingState.collect {
            val bodyLayout = binding.topSheet.topSheetBody
            bodyLayout.removeAllViews()

            val trainView = when (it) {
                TRAIN_INFO -> TrainInfoChartView(requireContext())
                TRAIN_RESULT -> TrainResultView(requireContext())
                TRAIN_REST_MESSAGE -> TrainRestMessageView(requireContext())
                CREATE_PLAN -> CreatePlanButton(requireContext()).also { setTopSheetHalfExpand() }
                else -> return@collect
            }

            bodyLayout.addView(trainView)
            bodyLayout.doOnNextLayout {
                if (bodyLayout.measuredHeight != 0) {
                    behavior.setHalfHeight(bodyLayout.measuredHeight)
                }
            }

            //TODO 추후 로직 때문에 기존 로직을 주석으로 담습니다.
            //TODO 아래 로직을 참고해서 화면에 보여질 리포트를 만들어주시면 됩니다.
//            topSheetBodyLayout.apply {
//                removeAllViews()
//                val trainInfoView = when (it) {
//                    1 -> TrainInfoChartView(context).also {
//                        makeChart(it.findViewById(R.id.barChart))
//                        topSheetBehavior.peekHeight = 700
//                    }
//
//                    2 -> TrainResultView(context).also {
//                        setPieChart(it.findViewById(R.id.chart_pace), 75f)
//                        setPieChart(it.findViewById(R.id.chart_heart), 60f)
//                        setPieChart(it.findViewById(R.id.chart_step), 70f)
//                        topSheetBehavior.peekHeight = 900
//                    }
//
//                    3 -> TrainRestMessageView(context).also {
//                        topSheetBehavior.peekHeight = 250
//                    }
//
//                    4 -> CreatePlanButton(context).also {
//                        when (viewModel.coachState.value) {
//                            1 -> R.drawable.coach_mike_white
//                            2 -> R.drawable.coach_jamie_white
//                            3 -> R.drawable.coach_danny_white
//                            else -> return@also
//                        }.run {
//                            it.setIconResource(this)
//                        }
//
//                        it.setOnClickListener {
//                            val action =
//                                HomeFragmentDirections.actionHomeFragmentToStartPlanFragment()
//                            findNavController().navigate(action)
//                        }
//
//                        topSheetBehavior.peekHeight = 400
//                    }
//
//                    else -> return@collect
//                }
//                addView(trainInfoView)
//            }
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
                        TopSheetBehavior.STATE_COLLAPSED -> {
                            topSheetBody.visibility = View.GONE
                        }

                        else -> {
                            topSheetBody.visibility = View.VISIBLE
                        }
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
        val today = LocalDate.now()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(1)
        val endMonth = currentMonth.plusMonths(1)
        val daysOfWeek = daysOfWeek()
        val weekCalendarView = binding.topSheet.weekCalendar

        weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
            override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.apply {
                    day = data
                    textView.text = data.date.dayOfMonth.toString()
                    if (data.date == today) container.ly.setBackgroundResource(R.drawable.day_selected_bg)
                    setOnClickListener { showSnackStringBar("다른날짜 로직 넣으세욤") }
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
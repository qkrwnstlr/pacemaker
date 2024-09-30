package com.ssafy.presentation.runningUI

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.LocationData
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRunningBinding
import com.ssafy.presentation.utils.formatCalories
import com.ssafy.presentation.utils.formatDistanceKm
import com.ssafy.presentation.utils.formatElapsedTime
import com.ssafy.presentation.utils.formatHeartRate
import com.ssafy.presentation.utils.formatPace
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration

private const val TAG = "RunningFragment_PACEMAKER"

@AndroidEntryPoint
class RunningFragment : BaseFragment<FragmentRunningBinding>(FragmentRunningBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: RunningViewModel by viewModels()
    private var map: GoogleMap? = null
    private var myLocationListener: LocationListener? = null
    private lateinit var onBackPressed: OnBackPressedCallback
    private var doubleBackToExitPressedOnce = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.startExercise()
        initView()
        initCollect()
        initListener()
        initMapView()
    }

    private fun initListener() = with(binding) {
        btnPause.setOnClickListener {
            viewModel.pauseExercise()
        }

        btnPlay.setOnClickListener {
            viewModel.resumeExercise()
        }

        btnStop.setOnClickListener {
            viewModel.endExercise()
        }

        btnMap.setOnClickListener {
            btnMap.showAnimate(false)
            btnInfo.showAnimate(true)
            runningMap.root.visibility = View.VISIBLE
            runningText.root.visibility = View.INVISIBLE
        }

        btnInfo.setOnClickListener {
            btnInfo.showAnimate(false)
            btnMap.showAnimate(true)
            runningText.root.visibility = View.VISIBLE
            runningMap.root.visibility = View.INVISIBLE
        }
    }

    private fun initView() {
        initRunningTextView()
        initRunningMapView()
    }


    private fun initRunningTextView() = with(binding.runningText.runningInfo) {
        boxBpm.tvRunningTitle.text = "심박수"
        boxKcal.tvRunningTitle.text = "총 소모 칼로리"
        boxPace.tvRunningTitle.text = "페이스"
        boxTime.tvRunningTitle.text = "총 시간"
    }

    private fun initRunningMapView() = with(binding.runningMap.runningInfo) {
        boxBpm.tvRunningTitle.text = "심박수"
        boxKcal.tvRunningTitle.text = "총 소모 칼로리"
        boxPace.tvRunningTitle.text = "페이스"
        boxTime.tvRunningTitle.text = "총 시간"
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectTrainingState()
        }
    }

    private fun CoroutineScope.collectTrainingState() = launch {
        viewModel.uiState.collect {
            if (it.isPaused) {
                with(binding) {
                    btnPlay.showAnimate(true)
                    btnStop.showAnimate(true)
                    btnPause.showAnimate(false)
                }
            }

            if (it.isResuming) {
                with(binding) {
                    btnPause.showAnimate(true)
                    btnPlay.showAnimate(false)
                    btnStop.showAnimate(false)
                }
            }

            if (it.isEnded) {
                showSnackStringBar("훈련 종료!")
                findNavController().popBackStack()
            }

            if(it.isActive) {
                it.exerciseState?.let { exerciseState ->
                    binding.runningText.tvDistance.text =
                        formatDistanceKm(exerciseState.exerciseMetrics.distance)

                    val duration = if (exerciseState.activeDurationCheckpoint != null) {
                        exerciseState.activeDurationCheckpoint.activeDuration.plusMillis(
                            System.currentTimeMillis() - exerciseState.activeDurationCheckpoint.time.toEpochMilli()
                        )
                    } else {
                        Duration.ZERO
                    }

                    with(binding.runningText.runningInfo) {
                        boxBpm.tvRunningContent.text = formatHeartRate(exerciseState.exerciseMetrics.heartRate)
                        boxKcal.tvRunningContent.text = formatCalories(exerciseState.exerciseMetrics.calories)
                        boxPace.tvRunningContent.text = formatPace(exerciseState.exerciseMetrics.pace)
                        boxTime.tvRunningContent.text = formatElapsedTime(duration, true)
                        exerciseState.exerciseMetrics.location?.let { it1 -> addMarker(it1) }
                    }
                    with(binding.runningMap.runningInfo) {
                        boxBpm.tvRunningContent.text = formatHeartRate(exerciseState.exerciseMetrics.heartRate)
                        boxKcal.tvRunningContent.text = formatCalories(exerciseState.exerciseMetrics.calories)
                        boxPace.tvRunningContent.text = formatPace(exerciseState.exerciseMetrics.pace)
                        boxTime.tvRunningContent.text = formatElapsedTime(duration, true)
                        exerciseState.exerciseMetrics.location?.let { it1 -> addMarker(it1) }
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    viewModel.endExercise()
                    return
                }

                doubleBackToExitPressedOnce = true
                showSnackStringBar("한번더 뒤로가기를 누르면 훈련이 종료됩니다.")

                Handler(Looper.getMainLooper()).postDelayed(
                    { doubleBackToExitPressedOnce = false },
                    2000
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressed)
    }

    private fun addMarker(location: LocationData) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        map?.animateCamera(cameraUpdate)

        val circleBitmap = createCircleBitmap(Color.RED, 10) // 빨간색, 반지름 50
        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromBitmap(circleBitmap))
        map?.addMarker(markerOptions)

        showSnackStringBar("위도 : ${location.latitude}, 경도 : ${location.longitude}")
    }

    private fun initMapView() {
        val mapFragment = SupportMapFragment.newInstance()
        getParentFragmentManager()
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // TODO : 두번째 키면 binding null pointer exception 해결하기
        getMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        //todo: 전역으로 데이터 저장해놓고, 러닝시 초기 화면은 그 값 받아와서 표시하고 이 set, get location은 없애기
        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        myLocationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                setMyLocation(p0)
            }
        }.apply {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    this
                )
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

    fun setMyLocation(location: Location) {
        if (myLocationListener != null) {
            val locationManager =
                requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(myLocationListener!!)
            myLocationListener = null
        }

        val latLng = LatLng(location.latitude, location.longitude)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)

        map?.animateCamera(cameraUpdate)
        val circleBitmap = createCircleBitmap(Color.RED, 10) // 빨간색, 반지름 50
        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromBitmap(circleBitmap))
        map?.addMarker(markerOptions)

        showSnackStringBar("위도 : ${location.latitude}, 경도 : ${location.longitude}")

    }

    fun createCircleBitmap(color: Int, radius: Int): Bitmap {
        val diameter = radius * 2
        val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            this.color = color
            this.isAntiAlias = true
        }
        canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), paint)
        return bitmap
    }

    private fun ImageButton.showAnimate(isShow: Boolean) {
        val alphaValue = if (isShow) 1f else 0f
        animate().apply {
            duration = 300
            alpha(alphaValue)

            withStartAction {
                if (isShow) visibility = View.VISIBLE
            }

            withEndAction {
                if (!isShow) visibility = View.INVISIBLE
            }
        }
    }
}
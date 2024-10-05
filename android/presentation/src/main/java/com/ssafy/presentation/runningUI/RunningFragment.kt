package com.ssafy.presentation.runningUI

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.health.services.client.data.LocationData
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRunningBinding
import com.ssafy.presentation.utils.formatCadenceRate
import com.ssafy.presentation.utils.formatDistance
import com.ssafy.presentation.utils.formatHeartRate
import com.ssafy.presentation.utils.formatSpeed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RunningFragment : BaseFragment<FragmentRunningBinding>(FragmentRunningBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: RunningViewModel by viewModels()
    private var map: GoogleMap? = null
    private var myLocationListener: LocationListener? = null
    private lateinit var onBackPressed: OnBackPressedCallback
    private var doubleBackToExitPressedOnce = false

    private var marker: Marker? = null

    private var timer: ActiveDurationTimerController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initCollect()
        initListener()
        initMapView()
        timer = ActiveDurationTimerController(this, listOf(
            binding.runningText.runningInfo.boxTime.tvRunningContent,
            binding.runningMap.runningInfo.boxTime.tvRunningContent,
        ))
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

    private fun initRunningTextView() = with(binding.runningText) {
        tvDistance.text = "--"
        with(runningInfo) {
            boxBpm.tvRunningTitle.text = "심박수"
            boxCadence.tvRunningTitle.text = "케이던스"
            boxPace.tvRunningTitle.text = "페이스"
            boxTime.tvRunningTitle.text = "총 시간"

            boxBpm.tvRunningContent.text = "--"
            boxCadence.tvRunningContent.text = "--"
            boxPace.tvRunningContent.text = "--"
            boxTime.tvRunningContent.text = "--"
        }
    }

    private fun initRunningMapView() = with(binding.runningMap.runningInfo) {
        boxBpm.tvRunningTitle.text = "심박수"
        boxCadence.tvRunningTitle.text = "케이던스"
        boxPace.tvRunningTitle.text = "페이스"
        boxTime.tvRunningTitle.text = "총 시간"

        boxBpm.tvRunningContent.text = "--"
        boxCadence.tvRunningContent.text = "--"
        boxPace.tvRunningContent.text = "--"
        boxTime.tvRunningContent.text = "--"
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectTrainingState()
        }
    }

    private fun CoroutineScope.collectTrainingState() = launch {
        viewModel.uiState.collect {
            it.exerciseState?.run { timer?.updateExerciseState(exerciseState, activeDurationCheckpoint) }

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
            }

            if (it.isActive) {
                it.exerciseState?.let { exerciseState ->
                    binding.runningText.tvDistance.text = formatDistance(exerciseState.exerciseMetrics.distance)

                    with(binding.runningText.runningInfo) {
                        boxBpm.tvRunningContent.text = formatHeartRate(exerciseState.exerciseMetrics.heartRate)
                        boxCadence.tvRunningContent.text = formatCadenceRate(exerciseState.exerciseMetrics.cadence?.toInt())
                        boxPace.tvRunningContent.text = formatSpeed(exerciseState.exerciseMetrics.speed)
                    }
                    with(binding.runningMap.runningInfo) {
                        boxBpm.tvRunningContent.text = formatHeartRate(exerciseState.exerciseMetrics.heartRate)
                        boxCadence.tvRunningContent.text = formatCadenceRate(exerciseState.exerciseMetrics.cadence?.toInt())
                        boxPace.tvRunningContent.text = formatSpeed(exerciseState.exerciseMetrics.speed)
                        exerciseState.exerciseMetrics.location?.let { it1 -> addPolyline(it1) }
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

    private fun addPolyline(location: LocationData) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        map?.animateCamera(cameraUpdate)

        val polylineOptions = PolylineOptions()
            .color(Color.BLUE)
            .width(10f)
            .addAll(listOf(marker?.position ?: latLng, latLng))

        map?.addPolyline(polylineOptions)
        marker?.position = latLng
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
        getMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
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
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker)
        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, MARKER_WIDTH, MARKER_HEIGHT, false)
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeBitmap)

        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(bitmapDescriptor)
        marker = map?.addMarker(markerOptions)
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

    companion object {
        const val MARKER_WIDTH = 200
        const val MARKER_HEIGHT = 200
    }
}
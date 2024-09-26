package com.ssafy.presentation.runningUI

import android.content.Context.LOCATION_SERVICE
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
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

@AndroidEntryPoint
class RunningFragment : BaseFragment<FragmentRunningBinding>(FragmentRunningBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: RunningViewModel by viewModels()
    private lateinit var map:GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectTrainingState()
            collectTrainingLocation()
        }
    }

    private fun CoroutineScope.collectTrainingState() = launch {
        viewModel.exerciseState.collect {
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

            it.exerciseState?.let { exerciseState ->
                binding.runningText.tvDistance.text =
                    formatDistanceKm(exerciseState.exerciseMetrics.distance)
                with(binding.runningText.runningInfo) {
                    boxBpm.tvRunningContent.text =
                        formatHeartRate(exerciseState.exerciseMetrics.heartRate)
                    boxKcal.tvRunningContent.text =
                        formatCalories(exerciseState.exerciseMetrics.calories)
                    boxPace.tvRunningContent.text =
                        formatPace(exerciseState.exerciseMetrics.pace)
                    boxTime.tvRunningContent.text =
                        formatElapsedTime(exerciseState.activeDurationCheckpoint?.activeDuration)
                }
            }
        }
    }

    private fun CoroutineScope.collectTrainingLocation() = launch {
        viewModel.exerciseMonitor.exerciseSessionData.collect{ result ->
            for(locationData in result){
                locationData.location?.let { addMarker(it) }
            }
        }
    }

    private fun addMarker(location: LocationData){
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        map.animateCamera(cameraUpdate)

        val circleBitmap = createCircleBitmap(Color.RED, 10) // 빨간색, 반지름 50
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(circleBitmap))
        map.addMarker(markerOptions)

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
        map=googleMap
        getMyLocation()
    }
    private var myLocationListener: LocationListener? = null
    private fun getMyLocation(){
            //todo: 전역으로 데이터 저장해놓고, 러닝시 초기 화면은 그 값 받아와서 표시하고 이 set, get location은 없애기
            val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
            myLocationListener = object : LocationListener {
                override fun onLocationChanged(p0: Location) {
                    setMyLocation(p0)
                }
            }

            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, myLocationListener!!)
            }

            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, myLocationListener!!)
            }

    }

    fun setMyLocation(location: Location){
        if(myLocationListener != null) {
            val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(myLocationListener!!)
            myLocationListener = null
        }

        val latLng = LatLng(location.latitude, location.longitude)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)

        map.animateCamera(cameraUpdate)
        val circleBitmap = createCircleBitmap(Color.RED, 10) // 빨간색, 반지름 50
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(circleBitmap))
        map.addMarker(markerOptions)

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
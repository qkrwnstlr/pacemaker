package com.ssafy.presentation.runningUI

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRunningBinding
import com.ssafy.presentation.utils.formatCalories
import com.ssafy.presentation.utils.formatDistanceKm
import com.ssafy.presentation.utils.formatElapsedTime
import com.ssafy.presentation.utils.formatHeartRate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RunningFragment : BaseFragment<FragmentRunningBinding>(FragmentRunningBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: RunningViewModel by viewModels()

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
                        formatCalories(exerciseState.exerciseMetrics.pace)
                    boxTime.tvRunningContent.text =
                        formatElapsedTime(exerciseState.activeDurationCheckpoint?.activeDuration)
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val point = LatLng(37.514655, 126.979974)
        map.addMarker(MarkerOptions().position(point).title("현위치"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 12f))
    }

    private fun initMapView() {
        val mapFragment = SupportMapFragment.newInstance()
        getParentFragmentManager()
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)
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
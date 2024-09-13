package com.ssafy.presentation.runningUI

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentRunningBinding

class RunningFragment : BaseFragment<FragmentRunningBinding>(FragmentRunningBinding::inflate),
    OnMapReadyCallback {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initListener()
        initMapView()
    }

    private fun initListener() = with(binding) {
        btnPause.setOnClickListener {
            btnPlay.showAnimate(true)
            btnStop.showAnimate(true)
            btnPause.showAnimate(false)
        }

        btnPlay.setOnClickListener {
            btnPause.showAnimate(true)
            btnPlay.showAnimate(false)
            btnStop.showAnimate(false)
        }

        btnStop.setOnClickListener {
            showSnackStringBar("훈련 종료!")
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
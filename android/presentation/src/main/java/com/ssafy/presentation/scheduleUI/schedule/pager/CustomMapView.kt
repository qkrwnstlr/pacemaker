package com.ssafy.presentation.scheduleUI.schedule.pager

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.CustomMapViewBinding

class CustomMapView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs),
    OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private val binding: CustomMapViewBinding by lazy {
        CustomMapViewBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.custom_map_view, this, false)
        )
    }

    init {
        initView()
    }

    private fun initView() {
        addView(binding.root)
        mapView = binding.map
        mapView?.onCreate(null)
        mapView?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    fun setMapView(locations: List<List<Double>>) {
        map?.clear()

        val path = mutableListOf<LatLng>()

        locations.forEach { location ->
            path.add(LatLng(location[0], location[1]))
        }

        drawPathOnMap(path)

        if (locations.isNotEmpty()) {
            val firstLocation = locations.average()
            val latLng = LatLng(firstLocation[0], firstLocation[1])
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    private fun drawPathOnMap(path: List<LatLng>) {
        val polylineOptions = PolylineOptions()
            .color(Color.BLUE)
            .width(10f)
            .addAll(path)

        map?.addPolyline(polylineOptions)
    }
}

fun List<List<Double>>.average(): List<Double> {
    val result = mutableListOf<Double>()
    for (i in 0 until this[0].size) result.add(this.map { it[i] }.average())
    return result
}
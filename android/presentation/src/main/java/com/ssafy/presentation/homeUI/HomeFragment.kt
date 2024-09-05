package com.ssafy.presentation.homeUI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
  OnMapReadyCallback {
  private lateinit var topSheetBehavior: TopSheetBehavior<LinearLayout>
  private val bottomSheetLayout by lazy { view?.findViewById<LinearLayout>(R.id.top_sheet_layout) }

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
    val topSheetBehavior = TopSheetBehavior.from(bottomSheetLayout!!)
    topSheetBehavior.setHideable(false); // STATE_HIDDEN 비활성화

    topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
      override fun onStateChanged(topSheet: View, newState: Int) {
        // BottomSheetBehavior의 5가지 상태
        when (newState) {

          // 사용자가 BottomSheet를 위나 아래로 드래그 중인 상태
          TopSheetBehavior.STATE_DRAGGING -> {}

          // 드래그 동작 후 BottomSheet가 특정 높이로 고정될 때의 상태
          // SETTLING 후 EXPANDED, SETTLING 후 COLLAPSED, SETTLING 후 HIDDEN
          TopSheetBehavior.STATE_SETTLING -> {}

          // 최대 높이로 보이는 상태
          TopSheetBehavior.STATE_EXPANDED -> {}

          // peek 높이 만큼 보이는 상태
          TopSheetBehavior.STATE_COLLAPSED -> {}
        }
      }

      override fun onSlide(p0: View, slideOffset: Float) {
        // slideOffset 범위: -1.0 ~ 1.0
        // -1.0 HIDDEN, 0.0 COLLAPSED, 1.0 EXPANDED
      }
    })
  }
}
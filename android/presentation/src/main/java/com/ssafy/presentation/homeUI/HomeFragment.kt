package com.ssafy.presentation.homeUI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
import com.ssafy.presentation.databinding.FragmentHomeBinding
import com.ssafy.presentation.loginUI.join.JoinRegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
  OnMapReadyCallback {
  private lateinit var topSheetBehavior: TopSheetBehavior<LinearLayout>
  private val topSheetLayout by lazy { view?.findViewById<LinearLayout>(R.id.top_sheet_layout) }
  private val topSheetBodyLayout by lazy { topSheetLayout?.findViewById<LinearLayout>(R.id.top_sheet_body) }

  private val viewModel: HomeViewModel by viewModels()

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
    topSheetBehavior = TopSheetBehavior.from(topSheetLayout!!)
    topSheetBehavior.setHideable(false)
    topSheetBodyLayout?.visibility = View.GONE

    topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
      override fun onStateChanged(topSheet: View, newState: Int) {
        when (newState) {
          // 사용자가 BottomSheet를 위나 아래로 드래그 중인 상태
          TopSheetBehavior.STATE_DRAGGING -> {
            topSheetBodyLayout?.visibility = View.VISIBLE
          }

          // 드래그 동작 후 BottomSheet가 특정 높이로 고정될 때의 상태
          // SETTLING 후 EXPANDED, SETTLING 후 COLLAPSED, SETTLING 후 HIDDEN
          TopSheetBehavior.STATE_SETTLING -> {}

          // 최대 높이로 보이는 상태
          TopSheetBehavior.STATE_EXPANDED -> {}

          // peek 높이 만큼 보이는 상태
          TopSheetBehavior.STATE_COLLAPSED -> {
            topSheetBodyLayout?.visibility = View.GONE
          }
        }
      }

      var oldOffset = -1f
      override fun onSlide(topSheet: View, slideOffset: Float) {
        if (oldOffset > slideOffset) {
          topSheetBodyLayout?.visibility = View.GONE
        }
        oldOffset = slideOffset
      }
    })

    initCollect()
  }

  private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      collectTrainingState()
    }
  }

  private fun CoroutineScope.collectTrainingState() = launch {
    viewModel.trainingState.collect { training ->
      topSheetBodyLayout?.let {
        it.removeAllViews()
        // TODO : 상태에 맞는 적절한 view로 수정
        val textView = TextView(it.context).apply {
          text = if (training) "Is Finished" else "Is not Finished"
          textSize = 18f
          setPadding(16, 16, 16, 16)
        }
        it.addView(textView)
      }
    }
  }
}
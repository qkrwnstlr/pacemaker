package com.ssafy.presentation.homeUI

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
  OnMapReadyCallback {
  private lateinit var topSheetBehavior: TopSheetBehavior<ConstraintLayout>
  private val topSheetLayout by lazy { view?.findViewById<ConstraintLayout>(R.id.top_sheet_layout) }
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
    topSheetBodyLayout?.visibility = View.GONE

    topSheetBehavior = TopSheetBehavior.from(topSheetLayout!!).apply {
      setHideable(false)
      setHalfable(true)
      setHalfHeight(1500)
    }

    topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
      override fun onStateChanged(topSheet: View, newState: Int) {
        when (newState) {
          TopSheetBehavior.STATE_COLLAPSED -> {
            topSheetBodyLayout?.visibility = View.GONE
          }

          TopSheetBehavior.STATE_EXPANDED -> {
            // TODO : Plan UI로 옮기기
          }

          else -> {
            topSheetBodyLayout?.visibility = View.VISIBLE
          }
        }
      }

      override fun onSlide(topSheet: View, slideOffset: Float) {
        if (slideOffset != 0f) {
          topSheetBodyLayout?.let {
            it.layoutParams.height =
              ((topSheet.height - topSheetBehavior.peekHeight) * slideOffset).toInt()
            it.requestLayout()
          }
        }
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
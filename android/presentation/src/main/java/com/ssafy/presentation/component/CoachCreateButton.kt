package com.ssafy.presentation.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ButtonCreatePlanBinding

class CoachCreateButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  private val binding by lazy {
    ButtonCreatePlanBinding.bind(
      LayoutInflater.from(context).inflate(R.layout.button_create_plan, this, true)
    )
  }

  fun setIconResource(iconResourceId: Int) = binding.createPlanButton.setIconResource(iconResourceId)
}
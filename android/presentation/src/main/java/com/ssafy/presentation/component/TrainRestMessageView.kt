package com.ssafy.presentation.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.ssafy.presentation.R

class TrainRestMessageView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  init {
    LayoutInflater.from(context).inflate(R.layout.train_rest_view, this, true)
  }
}
package com.ssafy.presentation.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.ssafy.presentation.databinding.ButtonCreatePlanBinding

class CreatePlanButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ButtonCreatePlanBinding = ButtonCreatePlanBinding.inflate(
        LayoutInflater.from(context), this
    )

    fun setIconResource(iconResourceId: Int) =
        binding.createPlanButton.setIconResource(iconResourceId)

    override fun setOnClickListener(l: OnClickListener?) =
        binding.createPlanButton.setOnClickListener(l)
}
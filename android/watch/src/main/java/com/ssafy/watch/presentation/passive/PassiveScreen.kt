package com.ssafy.watch.presentation.passive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import com.ssafy.watch.presentation.component.HeartRateCard

@Composable
fun PassiveScreen(modifier: Modifier = Modifier) {
    val viewModel: PassiveDataViewModel = hiltViewModel<PassiveDataViewModel>()
    val hrState by viewModel.hrValue.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        TimeText()
        HeartRateCard(heartRate = hrState)
    }
}
package com.ssafy.pacemaker.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.ssafy.pacemaker.R
import com.ssafy.pacemaker.presentation.component.button.StartButton
import com.ssafy.pacemaker.presentation.component.layout.Center

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onStart: () -> Unit,
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    viewModel.collectServiceState(onStart)

    HomeScreen(
        onStartClick = { viewModel.startExercise() }
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit,
) {
    TimeText(modifier = Modifier.padding(10.dp))
    Center {
        Image(
            painter = painterResource(id = R.drawable.logo_background),
            contentDescription = stringResource(id = R.string.logo_background_image_cd),
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(top = 20.dp, bottom = 40.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = R.string.logo_image_cd),
            contentScale = ContentScale.Fit,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.height(10.dp))
        StartButton(onStartClick)
    }
}

@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true
)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onStartClick = {}
    )
}
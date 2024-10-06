package com.ssafy.pacemaker.presentation.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.tooling.preview.devices.WearDevices
import com.ssafy.pacemaker.presentation.component.button.DesignedButton

@Composable
fun ResultRoute() {
    val viewModel = hiltViewModel<ResultViewModel>()

    ResultScreen(
        onFinish = { viewModel.onFinish() }
    )
}

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "운동이 종료되었습니다.", textAlign = TextAlign.Center, color = Color.White)
        Text(text = "휴대폰에서 결과를 확인하세요.", textAlign = TextAlign.Center, color = Color.White)
        DesignedButton(onClick = onFinish) {
            Text(text = "확인", color = Color.White)
        }
    }
}

@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true
)
@Composable
fun ResultScreenPreview() {
    ResultScreen(
        onFinish = {}
    )
}
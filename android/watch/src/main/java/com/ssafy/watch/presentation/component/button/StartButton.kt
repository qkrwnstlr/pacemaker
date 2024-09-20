@file:OptIn(ExperimentalHorologistApi::class)

package com.ssafy.watch.presentation.component.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button
import com.ssafy.watch.R

@Composable
fun StartButton(onStartClick: () -> Unit) {
    Button(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = stringResource(id = R.string.start_button_cd),
        onClick = onStartClick
    )
}

@Preview
@Composable
fun StartButtonPreview() {
    StartButton { }
}

@file:OptIn(ExperimentalHorologistApi::class)

package com.ssafy.pacemaker.presentation.component.button

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.ssafy.pacemaker.R

@Composable
fun StopButton(onEndClick: () -> Unit) {
    DesignedIconButton(
        modifier = Modifier
            .height(50.dp)
            .width(50.dp),
        onClick = onEndClick
    ) {
        Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = stringResource(id = R.string.pause_button_cd),
        )
    }
}

@Preview
@Composable
fun StopButtonPreview() {
    StopButton { }
}

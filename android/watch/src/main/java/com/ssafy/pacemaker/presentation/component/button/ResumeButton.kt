@file:OptIn(ExperimentalHorologistApi::class)

package com.ssafy.pacemaker.presentation.component.button

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.ssafy.pacemaker.R
import com.ssafy.pacemaker.presentation.theme.PaceMakerTheme

@Composable
fun ResumeButton(onResumeClick: () -> Unit) {
    DesignedIconButton(
        modifier = Modifier
            .height(50.dp)
            .width(50.dp),
        onClick = onResumeClick
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = stringResource(id = R.string.pause_button_cd),
        )
    }
}

@Preview
@Composable
fun ResumeButtonPreview() {
    PaceMakerTheme {
        ResumeButton { }
    }
}

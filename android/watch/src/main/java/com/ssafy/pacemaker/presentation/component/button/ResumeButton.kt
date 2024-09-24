@file:OptIn(ExperimentalHorologistApi::class)

package com.ssafy.pacemaker.presentation.component.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button
import com.ssafy.pacemaker.R
import com.ssafy.pacemaker.presentation.theme.PaceMakerTheme

@Composable
fun ResumeButton(onResumeClick: () -> Unit) {
    Button(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = stringResource(id = R.string.resume_button_cd),
        onClick = onResumeClick
    )
}

@Preview
@Composable
fun ResumeButtonPreview() {
    PaceMakerTheme {
        ResumeButton { }
    }
}

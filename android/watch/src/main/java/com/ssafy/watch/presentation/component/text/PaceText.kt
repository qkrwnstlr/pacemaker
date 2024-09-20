package com.ssafy.watch.presentation.component.text

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.ssafy.watch.R

@Composable
fun PaceText(pace: Double?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = stringResource(id = R.string.duration)
        )
        Text(text = pace?.toString() ?: "--")
    }
}

@Preview
@Composable
fun PaceTextPreview() {
    PaceText(pace = 100.0)
}
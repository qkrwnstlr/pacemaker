package com.ssafy.pacemaker.presentation.component.text

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.ssafy.pacemaker.R
import com.ssafy.pacemaker.utils.formatElapsedTime
import java.time.Duration

@Composable
fun DurationText(duration: Duration?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.WatchLater,
            contentDescription = stringResource(id = R.string.duration)
        )
        Text(text = formatElapsedTime(duration, true))
    }
}

@Preview
@Composable
fun TimeTextPreview() {
    DurationText(duration = Duration.ofSeconds(107))
}
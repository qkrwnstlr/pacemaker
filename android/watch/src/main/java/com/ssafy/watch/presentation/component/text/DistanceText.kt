package com.ssafy.watch.presentation.component.text

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.ssafy.watch.R
import com.ssafy.watch.utils.formatDistanceKm

@Composable
fun DistanceText(distance: Double?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.TrendingUp,
            contentDescription = stringResource(id = R.string.distance)
        )
        Text(text = formatDistanceKm(distance))
    }
}

@Preview
@Composable
fun DistanceTextPreview() {
    DistanceText(distance = 505.0)
}

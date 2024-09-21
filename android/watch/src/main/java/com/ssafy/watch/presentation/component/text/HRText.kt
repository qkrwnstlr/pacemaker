package com.ssafy.watch.presentation.component.text

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.ssafy.watch.R
import com.ssafy.watch.utils.formatHeartRate

@Composable
fun HRText(hr: Double?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = stringResource(id = R.string.heart_rate)
        )
        Text(text = formatHeartRate(hr))
    }
}

@Preview
@Composable
fun HRTextPreview() {
    HRText(hr = 80.0)
}

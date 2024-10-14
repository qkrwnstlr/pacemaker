package com.ssafy.pacemaker.presentation.component.text

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.ssafy.pacemaker.R
import com.ssafy.pacemaker.utils.formatCadenceRate

@Composable
fun CadenceText(cadence: Long?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.DirectionsRun,
            contentDescription = stringResource(id = R.string.calories)
        )
        Text(text = formatCadenceRate(cadence))
    }
}

@Preview
@Composable
fun CadenceTextPreview() {
    CadenceText(cadence = null)
}

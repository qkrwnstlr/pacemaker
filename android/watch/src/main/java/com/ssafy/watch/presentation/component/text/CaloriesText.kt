package com.ssafy.watch.presentation.component.text

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.ssafy.watch.R
import com.ssafy.watch.utils.formatCalories

@Composable
fun CaloriesText(calories: Double?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = stringResource(id = R.string.calories)
        )
        Text(text = formatCalories(calories))
    }
}

@Preview
@Composable
fun CaloriesTextPreview() {
    CaloriesText(calories = null)
}

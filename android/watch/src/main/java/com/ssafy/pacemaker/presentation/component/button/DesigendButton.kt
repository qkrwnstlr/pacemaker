package com.ssafy.pacemaker.presentation.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ssafy.pacemaker.presentation.theme.LightColors

@Composable
fun DesignedButton(onClick: () -> Unit, content: @Composable() (RowScope.() -> Unit)) {
    Button(
        modifier = Modifier.defaultMinSize(1.dp, 1.dp),
        contentPadding = PaddingValues(10.dp, 5.dp),
        colors = ButtonColors(
            contentColor = Color.White,
            containerColor = LightColors.primary,
            disabledContentColor = Color.White,
            disabledContainerColor = Color.Gray
        ),
        onClick = onClick,
        content = content
    )
}
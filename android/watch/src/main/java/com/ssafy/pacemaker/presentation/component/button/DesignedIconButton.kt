package com.ssafy.pacemaker.presentation.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ssafy.pacemaker.presentation.theme.LightColors

@Composable
fun DesignedIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable() (RowScope.() -> Unit)
) {
    Button(
        modifier = modifier.defaultMinSize(1.dp, 1.dp),
        contentPadding = PaddingValues(10.dp, 5.dp),
        colors = ButtonColors(
            contentColor = Color.White,
            containerColor = LightColors.secondary,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Transparent
        ),
        onClick = onClick,
        content = content
    )
}
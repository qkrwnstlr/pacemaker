package com.ssafy.pacemaker.presentation.component

import android.graphics.Paint
import android.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun CurvedText(text: String, fontSize: TextUnit, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.wrapContentSize()) {
        drawIntoCanvas {
            val path = Path().apply {
                addArc(0f, 0f, fontSize.toPx() * text.length, fontSize.toPx(), 180f, 180f)
            }
            it.nativeCanvas.drawTextOnPath(
                text,
                path,
                0f,
                0f,
                Paint().apply {
                    textSize = fontSize.toPx()
                    textAlign = Paint.Align.CENTER
                }
            )
        }
    }
}

@Preview(
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true
)
@Composable
fun CurvedTextPreview() {
    CurvedText("안녕하세요", 16.sp)
}
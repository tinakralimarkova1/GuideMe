// LessonHighlightOverlay.kt
package com.example.guideme.lessons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp


@Composable
fun LessonHighlightOverlay(
    anchorId: String?,
    outlineColor: Color = Color(0xFFFFC107),   // amber outline
    outlineWidthDp: Float = 3f,
    fillColor: Color? = Color(0x33FFC107),     // soft translucent fill
    cornerRadiusDp: Float = 16f
) {
    if (anchorId == null) return

    // If a fully transparent outline is passed in (alpha == 0),
    // treat that as "no highlight at all" (no outline, no fill).
    if (outlineColor.alpha == 0f) return

    val coords: LayoutCoordinates = AnchorRegistry.anchors[anchorId] ?: return
    if (!coords.isAttached) return

    val density: Density = LocalDensity.current
    val topLeft: Offset = coords.positionInRoot()
    val sizePx = coords.size
    val rectSize = Size(sizePx.width.toFloat(), sizePx.height.toFloat())

    val outlineWidthPx: Float
    val cornerRadiusPx: Float
    with(density) {
        outlineWidthPx = outlineWidthDp.dp.toPx()
        cornerRadiusPx = cornerRadiusDp.dp.toPx()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Optional soft fill inside the outline
        if (fillColor != null) {
            drawRoundRect(
                color = fillColor,
                topLeft = topLeft,
                size = rectSize,
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )
        }

        // Crisp outline stroke
        drawRoundRect(
            color = outlineColor,
            topLeft = topLeft,
            size = rectSize,
            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
            style = Stroke(width = outlineWidthPx)
        )
    }
}
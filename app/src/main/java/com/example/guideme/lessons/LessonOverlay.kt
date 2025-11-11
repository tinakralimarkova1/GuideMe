package com.example.guideme.lessons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.Color

@Composable
fun LessonHighlightOverlay(
    anchorId: String?
) {
    if (anchorId == null) return

    val coords = AnchorRegistry.anchors[anchorId] ?: return

    // Convert 0,0 from local to window coordinates
    val topLeft: Offset = coords.localToWindow(Offset.Zero)
    val size = coords.size

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRoundRect(
            color = Color(0x55FFEB3B), // translucent yellow
            topLeft = topLeft,
            size = Size(size.width.toFloat(), size.height.toFloat()),
            cornerRadius = CornerRadius(24f, 24f)
        )
    }
}
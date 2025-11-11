package com.example.guideme.lessons

import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned

object AnchorRegistry {
    // Map anchorId -> layout coordinates of that UI element
    val anchors: MutableMap<String, LayoutCoordinates> = mutableMapOf()
}

fun Modifier.anchorId(id: String): Modifier {
    return this.then(
        Modifier.onGloballyPositioned { coords: LayoutCoordinates ->
            AnchorRegistry.anchors[id] = coords
        }
    )
}
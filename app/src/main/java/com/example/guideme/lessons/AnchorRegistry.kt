// AnchorRegistry.kt
package com.example.guideme.lessons

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned

object AnchorRegistry {
    // observable map: changes trigger recomposition
    val anchors = mutableStateMapOf<String, LayoutCoordinates>()
}

fun Modifier.anchorId(id: String): Modifier =
    this.then(
        Modifier.onGloballyPositioned { coords ->
            // only store valid coordinates
            if (coords.isAttached) {
                AnchorRegistry.anchors[id] = coords
            } else {
                AnchorRegistry.anchors.remove(id)
            }
        }
    )

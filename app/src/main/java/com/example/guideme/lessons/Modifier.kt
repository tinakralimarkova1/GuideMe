// In app/src/main/java/com/example/guideme/lessons/Modifiers.kt
package com.example.guideme.lessons

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * A modifier that briefly flashes a red background when the `tappedIncorrectAnchorId`
 * matches the `currentAnchorId` of this composable.
 */
@Composable
fun Modifier.flash(
    tappedIncorrectAnchorId: String? = null,
    currentAnchorId: String
): Modifier {
    val color = remember { Animatable(Color.Transparent) }

    // This LaunchedEffect will re-run whenever the incorrect ID changes.
    LaunchedEffect(tappedIncorrectAnchorId) {
        // If this button was the one that was just tapped incorrectly...
        if (tappedIncorrectAnchorId == currentAnchorId) {
            // ...animate its background to red and then back to transparent.
            color.animateTo(Color.Red.copy(alpha = 0.5f), animationSpec = tween(100))
            color.animateTo(Color.Transparent, animationSpec = tween(500))
        }
    }
    // Apply the animated background color to the modifier chain.
    return this.background(color.value)
}

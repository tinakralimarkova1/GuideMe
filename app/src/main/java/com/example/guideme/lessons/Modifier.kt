// In app/src/main/java/com/example/guideme/lessons/Modifiers.kt
package com.example.guideme.lessons

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.flash(
    tappedIncorrectAnchorId: String? = null,
    currentAnchorId: String
): Modifier {
    val color = remember { Animatable(Color.Transparent) }

    LaunchedEffect(tappedIncorrectAnchorId) {
        // Match IDs like "DialPad.Call#12345"
        val isForThisAnchor =
            tappedIncorrectAnchorId?.startsWith(currentAnchorId) == true

        if (isForThisAnchor) {
            color.animateTo(
                Color.Red.copy(alpha = 0.5f),
                animationSpec = tween(100)
            )
            color.animateTo(
                Color.Transparent,
                animationSpec = tween(500)
            )
        }
    }

    return this.then(
        Modifier.drawWithContent {
            drawContent()
            if (color.value.alpha > 0f) {
                drawRect(color.value)
            }
        }
    )
}

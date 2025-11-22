package com.example.guideme.lessons


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guideme.phone.CameraScreen
import com.example.guideme.phone.PhoneNavHost
import com.example.guideme.ui.theme.InstructionTextBoxColor
import com.example.guideme.ui.theme.MainBackgroundGradient
import com.example.guideme.ui.theme.MainButtonContentColor
import com.example.guideme.wifi.WifiNavHost


@Composable
fun LessonHost(
    appName: String,
    lessonId: Int,
    repo: LessonsRepository,
    userEmail: String,
    onExit: () -> Unit
) {
    val vm: LessonViewModel = viewModel(
        factory = LessonViewModelFactory(repo, userEmail)
    )

    val state = vm.uiState

    LaunchedEffect(appName, lessonId) { vm.loadLesson(appName, lessonId) }

    Box(Modifier.fillMaxSize().background(MainBackgroundGradient))
    {
        // 1) Your fake app UI (emit events back to ViewModel)
        if (state.completed) {
            // 🔁 Once done, show a *separate full-screen* screen
            LessonCompleteScreen(
                onExit = onExit
            )
        } else {
            // 🚧 Only show your fake UI while in-progress
            when (appName) {
                "Phone" -> PhoneNavHost(
                    onAnchorTapped = { anchorId ->
                        vm.onUserEvent(UserEvent.TapOnAnchor(anchorId))
                    },
                    onNumberCommitted = { text ->
                        vm.onUserEvent(UserEvent.TextEntered(text))
                    },
                    correctAnchor = state.correctAnchor,
                    tappedIncorrectAnchor = state.tappedIncorrectAnchorId,
                    isAnchorAllowed = { anchorId ->
                        vm.isButtonAllowed(anchorId)
                    }
                )
                "WiFi" -> WifiNavHost(
                    onAnchorTapped = { anchorId ->
                        vm.onUserEvent(UserEvent.TapOnAnchor(anchorId))
                    },
                   onToggle = { id, on ->
                       vm.onUserEvent(UserEvent.Toggle(id, on))
                   },
                    correctAnchor = state.correctAnchor,
                    tappedIncorrectAnchor = state.tappedIncorrectAnchorId,
                    isAnchorAllowed = { anchorId ->
                        vm.isButtonAllowed(anchorId)
                    },
                    defaultStates = state.defaultButtonStates
                )
                "Camera" -> CameraScreen(
                    onAnchorTapped = { anchorID ->
                        vm.onUserEvent(UserEvent.TapOnAnchor(anchorID))
                    },
                    correctAnchor = state.correctAnchor,
                    tappedIncorrectAnchor = state.tappedIncorrectAnchorId,
                    isAnchorAllowed = { anchorId ->
                        vm.isButtonAllowed(anchorId)
                    }

                )
                else -> Text("No fake UI for $appName")
            }

            // Instruction overlay + highlight while not completed
            if (state.steps.isNotEmpty()) {
                val current = state.steps[state.currentIndex]
                LessonHighlightOverlay(
                    anchorId = current.anchorId,
                    outlineColor = current.outlineColor?.let { Color(it) } ?: Color(0xFFFFC107)
                )
                if (current.type == StepType.Acknowledge) {
                    TapBlockerOverlay()
                }
                InstructionOverlay(
                    text = current.text,
                    feedback = if (current.type == StepType.Acknowledge) null else state.feedback,
                    showOk = current.type == StepType.Acknowledge,
                    onOk = if (current.type == StepType.Acknowledge) {
                        { vm.onUserEvent(UserEvent.Acknowledge) }
                    } else null
                )
            }
        }
    }
}


@Composable
private fun InstructionOverlay(
    text: String,
    feedback: String? = null,
    showOk: Boolean = false,
    onOk: (() -> Unit)? = null
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 55.dp,
                bottom = 10.dp       // 👈 prevents covering the Call button
            )
            ,

        contentAlignment = Alignment.BottomCenter
    ) {

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = InstructionTextBoxColor,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                    .fillMaxWidth()
                // 👇 FIXED SIZE BEHAVIOR
                .heightIn(min = 140.dp, max = 160.dp)


        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Instruction text
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MainButtonContentColor
                    ),
                    textAlign = TextAlign.Center
                )

                // Error / feedback text
                if (feedback != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                // OK button (for Acknowledge step)
                if (showOk && onOk != null) {
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = onOk,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(0.6f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCompleteScreen(onExit: () -> Unit) {
    // Full-screen, independent of the fake app UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Lesson complete!", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onExit) { Text("Back to menu") }
            }
        }
    }
}

@Composable
private fun TapBlockerOverlay() {
    Box(
        Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Intentionally empty – just eat taps
            }
    )
}


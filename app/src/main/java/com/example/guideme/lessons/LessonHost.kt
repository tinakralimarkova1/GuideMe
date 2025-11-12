package com.example.guideme.lessons


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guideme.ui.theme.MainBackgroundGradient
import com.example.guideme.phone.PhoneNavHost    // reuse your existing fake UI
import com.example.guideme.wifi.WifiNavHost
import com.example.guideme.phone.DialPadScreen   // if you want a single sub-screen
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController // SHORT TERM SOLUTION TODO: fix it
import androidx.compose.ui.graphics.Color


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
                    }
                )
                "WiFi" -> WifiNavHost(
                    // e.g. onToggle = { id, on -> vm.onUserEvent(UserEvent.Toggle(id, on)) }
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
                InstructionOverlay(
                    text = current.text,
                    feedback = state.feedback,
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
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(tonalElevation = 2.dp, shadowElevation = 6.dp) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text, style = MaterialTheme.typography.bodyLarge)
                if (feedback != null) {
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (showOk && onOk != null) {
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onOk) { Text("OK") }
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
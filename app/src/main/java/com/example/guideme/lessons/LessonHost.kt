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


@Composable
fun LessonHost(
    appName: String,
    lessonId: Int,
    repo: LessonsRepository,
    userEmail: String
) {
    val vm: LessonViewModel = viewModel(
        factory = LessonViewModelFactory(repo, userEmail)
    )

    val state = vm.uiState

    LaunchedEffect(appName, lessonId) { vm.loadLesson(appName, lessonId) }

    Box(Modifier.fillMaxSize().background(MainBackgroundGradient)) {
        // 1) Your fake app UI (emit events back to ViewModel)
        when (appName) {
            "Phone" -> PhoneNavHost(
                onAnchorTapped = { anchorId ->
                    vm.onUserEvent(UserEvent.TapOnAnchor(anchorId))
                },
                onNumberCommitted = { text ->
                    vm.onUserEvent(UserEvent.TextEntered(text))
                }
            )
            "WiFi" -> WifiNavHost(     // expose callbacks similarly inside your WiFi screens
                // e.g., onToggle = { id, on -> vm.onUserEvent(UserEvent.Toggle(id, on)) }
            )
            else -> Text("No fake UI for $appName")
        }

        // 2) Instruction overlay
        if (!state.completed && state.steps.isNotEmpty()) {
            val current = state.steps[state.currentIndex]
            LessonHighlightOverlay(
                anchorId = current.anchorId
            )
            InstructionOverlay(
                text = current.text,
                feedback = state.feedback
                // For now, we just show text; later you can map anchorId -> coordinates.
            )
        } else if (state.completed) {
            LessonComplete(onExit = { /* navigate back */ })
        }
    }
}

@Composable
private fun InstructionOverlay(text: String, feedback: String? = null) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            tonalElevation = 2.dp,
            shadowElevation = 6.dp
        ) {
            Text(
                text,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            if (feedback != null) {
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun LessonComplete(onExit: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Lesson complete!", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onExit) { Text("Back to menu") }
        }
    }
}

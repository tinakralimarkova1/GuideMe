package com.example.guideme.lessons


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guideme.phone.CameraScreen
import com.example.guideme.phone.PhoneNavHost
import com.example.guideme.ui.theme.InstructionTextBoxColor
import com.example.guideme.ui.theme.MainBackgroundGradient
import com.example.guideme.ui.theme.MainButtonColor
import com.example.guideme.ui.theme.MainButtonContentColor
import com.example.guideme.wifi.WifiNavHost
import kotlinx.coroutines.delay


@Composable
fun LessonHost(
    appName: String,
    lessonId: Int,
    repo: LessonsRepository,
    userEmail: String,
    onExit: () -> Unit,
    onStartLesson: (String, Int) -> Unit

) {
    val vm: LessonViewModel = viewModel(
        factory = LessonViewModelFactory(repo, userEmail)
    )

    val state = vm.uiState

    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appName, lessonId) { vm.loadLesson(appName, lessonId) }

    val soundEvent by vm.soundEvent.collectAsState()

    LaunchedEffect(soundEvent) {
        when (soundEvent) {
            is SoundEvent.Correct -> {
                Sfx.playCorrect()
                vm.clearSoundEvent()
            }

            is SoundEvent.Wrong   -> {
                Sfx.playWrong()
                vm.clearSoundEvent()
            }

            is SoundEvent.Complete ->{
                Sfx.playComplete()
                vm.clearSoundEvent()
            }

            is SoundEvent.Click ->{
                Sfx.playClick()
                vm.clearSoundEvent()
            }
            null -> {}
        }
    }


    Box(Modifier
        .fillMaxSize()
        .background(MainBackgroundGradient))
    {
        // 1) Your fake app UI (emit events back to ViewModel)
        if (state.completed) {
            // 🔁 Once done, show a *separate full-screen* screen
            LessonCompleteScreen(
                state = state,
                onExit = onExit,
                onStartLesson = onStartLesson
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
                    },
                    defaultStates = state.defaultButtonStates
                )
                "WiFi" -> WifiNavHost(
                    onAnchorTapped = { anchorId ->
                        vm.onUserEvent(UserEvent.TapOnAnchor(anchorId))
                    },
                   onToggle = { id, on ->
                       vm.onUserEvent(UserEvent.Toggle(id, on))
                   },
                    onNumberCommitted = { text ->
                        vm.onUserEvent(UserEvent.TextEntered(text))
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
                    },
                    defaultStates = state.defaultButtonStates

                )
                else -> Text("No fake UI for $appName")
            }

            // Instruction overlay + highlight while not completed
            if (state.steps.isNotEmpty()) {
                val current = state.steps[state.currentIndex]
                //show button
                LessonHighlightOverlay(
                    anchorId = current.anchorId,
                    outlineColor = current.outlineColor?.let { Color(it) } ?: Color(0xFFFFC107)
                )
                if (current.type == StepType.Acknowledge) {
                    TapBlockerOverlay()
                }
                //display instructions
                InstructionOverlay(
                    text = current.text,
                    showOk = current.type == StepType.Acknowledge,
                    onOk = if (current.type == StepType.Acknowledge) {
                        { vm.onUserEvent(UserEvent.Acknowledge) }
                    } else null
                )
                //display feedbacj
                if (state.feedback != null) {
                    FeedbackOverlay(
                        message = state.feedback,
                        onDismiss = { vm.clearFeedback() }
                    )
                }
                //back button
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(vertical = 30.dp, horizontal = 15.dp)
                        .height(35.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MainButtonContentColor)
                        .clickable { showExitDialog = true }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back to menu",
                        tint = MainButtonColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Back to Menu",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MainButtonColor,
                            fontSize = 20.sp
                        )
                    )
                }

            }
        }
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit lesson?") },
            text = {
                Text("If you leave now, your progress in this lesson will be lost.")
            },
            confirmButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("Continue lesson")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onExit()  // this should take you back to the menu
                    }
                ) {
                    Text("Exit to menu")
                }
            }
        )
    }


}


@Composable
private fun InstructionOverlay(
    text: String,
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
                .heightIn(min = 140.dp)


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
                        color = MainButtonContentColor,

                    ),
                    textAlign = TextAlign.Center
                )




                // OK button (for Acknowledge step)
                if (showOk && onOk != null) {
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = onOk,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(0.6f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainButtonContentColor,
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
fun LessonCompleteScreen(
    state: LessonState,
    onExit: () -> Unit,
    onStartLesson: (String, Int) -> Unit
) {
    // Outer Box: gradient already provided by LessonHost
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 460.dp),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Lesson complete!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MainButtonContentColor
                )

                Spacer(Modifier.height(12.dp))

                Text("Time spent: ${state.timeSpentSeconds ?: 0} seconds", color = MainButtonContentColor)
                Text("Errors: ${state.errorCount}", color = MainButtonContentColor)
                Text("Attempts: ${state.attempts}", color = MainButtonContentColor)

                Spacer(Modifier.height(30.dp))

                if (state.recommendedLessons.isNotEmpty()) {
                    Text(
                        text = "We recommend trying these next:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MainButtonContentColor
                    )
                    Spacer(Modifier.height(8.dp))

                    state.recommendedLessons.forEach { rec ->
                        Button(
                            onClick = { onStartLesson(rec.appName, rec.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainButtonContentColor,
                                contentColor = MainButtonColor
                            )
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(rec.name, style = MaterialTheme.typography.titleSmall)
                                Text(rec.appName, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                Button(
                    onClick = onExit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonContentColor,
                        contentColor = MainButtonColor
                    )
                ) {
                    Text("Back to lessons")
                }
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

@Composable
private fun FeedbackOverlay(
    message: String,
    onDismiss: () -> Unit,
    displayDurationMillis: Long = 1500L,
    fadeDurationMillis: Int = 300
) {
    var visible by remember(message) { mutableStateOf(true) }

    // Handle timing: show → wait → fade out → then clear feedback in VM
    LaunchedEffect(message) {

        visible = true                // ensure it's visible when message changes
        delay(displayDurationMillis)  // keep fully visible
        visible = false               // trigger fade out
        delay(fadeDurationMillis.toLong())
        onDismiss()                   // actually clear state.feedback in VM
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = fadeDurationMillis)),
            exit = fadeOut(animationSpec = tween(durationMillis = fadeDurationMillis))
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.96f),
                tonalElevation = 6.dp,
                shadowElevation = 10.dp
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

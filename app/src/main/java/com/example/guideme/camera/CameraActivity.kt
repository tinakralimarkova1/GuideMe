package com.example.guideme.camera

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.guideme.tts.TTS

class CameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // (Re)initialize TTS here so this screen can speak independently
        TTS.init(this) {
            TTS.speak("Camera guidance screen. I will show and read the steps to open your camera.")
        }

        setContent {
            Scaffold { inner ->
                CameraHelpScreen(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    onGuideMeThere = { goToHomeScreen() }
                )
            }
        }
    }

    private fun goToHomeScreen() {
        // Speak the short “action” summary first
        TTS.speak("I will minimize the app. Follow the steps on your screen to open the Camera.")

        // Send user to the phone’s Home screen so they can follow the steps live
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        // NOTE: We do NOT finish() here; if the user returns, the guidance remains open.
    }

    override fun onDestroy() {
        super.onDestroy()
        // Keep parity with your MainActivity lifecycle handling
        TTS.shutdown()
    }
}

@Composable
private fun CameraHelpScreen(
    modifier: Modifier = Modifier,
    onGuideMeThere: () -> Unit
) {
    val steps = listOf(
        "From the Home screen, look for the Camera icon.",
        "If you don’t see it, swipe up to open the App Drawer.",
        "Scroll until you find the Camera app.",
        "Tap the Camera icon once to open it."
    )

    // Read the steps once when the screen appears (helps users who rely on audio)
    LaunchedEffect(Unit) {
        TTS.speak(
            "Steps to open the Camera: " +
                    "One: From the Home screen, look for the Camera icon. " +
                    "Two: If you don't see it, swipe up to open the App Drawer. " +
                    "Three: Scroll until you find the Camera app. " +
                    "Four: Tap the Camera icon once to open it."
        )
    }

    Column(
        modifier = modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Open the Camera",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Follow these steps. You can tap “Guide me there” to minimize this app and do it.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(16.dp))

        steps.forEachIndexed { idx, s ->
            StepRow(number = idx + 1, text = s)
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onGuideMeThere) {
            Text("Guide me there")
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "Tip: If your phone has a Camera shortcut on the lock screen, " +
                    "you can press the power button to lock, then swipe the camera icon.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StepRow(number: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(28.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

package com.example.guideme

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Text-to-Speech
        TTS.init(this) {
            TTS.speak("Welcome to Guide Me. Please choose Camera, Phone, or Wi-Fi.")
        }

        setContent {
            GuideMeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TTS.shutdown()
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showDialogFor by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Button(onClick = {
            TTS.speak("Opening the in-app camera preview.")
            val intent = Intent(context, com.example.guideme.camera.CameraActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Camera")
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            TTS.speak("Phone is selected. Would you like me to open it for you, or guide you there?")
            showDialogFor = "phone"
        }) { Text("Phone") }

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            TTS.speak("Wi-Fi is selected. Would you like me to open it for you, or guide you there?")
            showDialogFor = "wifi"
        }) { Text("Wi-Fi") }
    }

    // --- Dialog logic ---
    showDialogFor?.let { choice ->
        AlertDialog(
            onDismissRequest = { showDialogFor = null },
            title = { Text("${choice.replaceFirstChar { it.uppercase() }} Options") },
            text = { Text("Would you like me to open the $choice app for you, or guide you there?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialogFor = null
                    when (choice) {
                        // ✅ CAMERA — open camera app directly
                        "camera" -> {
                            TTS.speak("Opening the camera app.")
                            try {
                                // ACTION_IMAGE_CAPTURE opens the device’s default camera app
                                val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
                                // Fallback if still image intent isn’t available
                                if (intent.resolveActivity(context.packageManager) == null) {
                                    // Some devices only support ACTION_IMAGE_CAPTURE
                                    val altIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    context.startActivity(altIntent)
                                } else {
                                    context.startActivity(intent)
                                }
                            } catch (e: Exception) {
                                TTS.speak("Sorry, I could not open the camera app.")
                                e.printStackTrace()
                            }
                        }

                        // ✅ PHONE — open dialer
                        "phone" -> {
                            TTS.speak("Opening the phone app.")
                            val intent = Intent(Intent.ACTION_DIAL)
                            context.startActivity(intent)
                        }

                        // ✅ WIFI — open settings
                        "wifi" -> {
                            TTS.speak("Opening Wi-Fi settings.")
                            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                }) { Text("Open the app for me") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        showDialogFor = null
                        TTS.speak("Guiding you to the home screen.")
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }) { Text("Guide me to the app") }

                    TextButton(onClick = {
                        showDialogFor = null
                        TTS.speak("Cancelled.")
                    }) { Text("Cancel") }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    GuideMeTheme {
        MainScreen()
    }
}

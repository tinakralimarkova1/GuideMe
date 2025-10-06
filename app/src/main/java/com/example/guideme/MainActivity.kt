package com.example.guideme

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.guideme.phone.PhoneScreen
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize TTS once for the app session
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

    // Simple screen switcher: "main" menu or "phone" screen
    var currentScreen by remember { mutableStateOf("main") }

    // Dialog only used for camera/wifi (we won’t use it for phone anymore)
    var showDialogFor by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        "main" -> {
            Column(modifier = modifier.fillMaxSize()) {
                // Camera
                Button(onClick = {
                    TTS.speak("Camera is selected. Would you like me to open it for you, or guide you there?")
                    showDialogFor = "camera"
                }) { Text("Camera") }

                // Phone → navigate to PhoneScreen
                Button(onClick = {
                    TTS.speak("Opening the phone feature.")
                    currentScreen = "phone"
                }) { Text("Phone") }

                // Wi-Fi
                Button(onClick = {
                    TTS.speak("Wi-Fi is selected. Would you like me to open it for you, or guide you there?")
                    showDialogFor = "wifi"
                }) { Text("Wi-Fi") }
            }

            // Camera / Wi-Fi dialog
            showDialogFor?.let { choice ->
                AlertDialog(
                    onDismissRequest = { showDialogFor = null },
                    title = { Text("${choice.replaceFirstChar { it.uppercase() }} Options") },
                    text = { Text("Would you like me to open the $choice app for you, or guide you there?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialogFor = null
                            when (choice) {
                                "camera" -> {
                                    TTS.speak("Opening the camera app.")
                                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    intent.resolveActivity(context.packageManager)?.let {
                                        context.startActivity(intent)
                                    }
                                }
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

        "phone" -> {
            // Show your Compose phone feature
            PhoneScreen()

            // Android back button returns to main menu
            BackHandler { currentScreen = "main" }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    GuideMeTheme {
        MainScreen()
    }
}

package com.example.guideme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.guideme.phone.PhoneNavHost
import com.example.guideme.phone.CameraScreen
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme
import com.example.guideme.wifi.WifiNavHost
import androidx.compose.ui.unit.dp


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
    var currentScreen by remember { mutableStateOf("main") }

    when (currentScreen) {
        "main" -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "GuideMe Training Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = {
                        TTS.speak("Opening Camera.")
                        currentScreen = "camera"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Camera")
                }

                Button(
                    onClick = {
                        TTS.speak("Opening Phone.")
                        currentScreen = "phone"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Phone")
                }

                Button(
                    onClick = {
                        TTS.speak("Opening Wi-Fi.")
                        currentScreen = "wifi"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Wi-Fi")
                }
            }
        }

        // Phone now loads the NavHost with bottom tabs
        "phone" -> {
            PhoneNavHost()
            BackHandler {
                TTS.speak("Returning to main menu.")
                currentScreen = "main"
            }
        }

        "camera" -> {
            CameraScreen()
            BackHandler {
                TTS.speak("Returning to main menu.")
                currentScreen = "main"
            }
        }

        "wifi" -> {
            WifiNavHost()
            BackHandler {
                TTS.speak("Returning to main menu.")
                currentScreen = "main"
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    GuideMeTheme { MainScreen() }
}


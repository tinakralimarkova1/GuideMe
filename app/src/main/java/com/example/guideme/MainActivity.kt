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
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.guideme.ui.theme.MainBackgroundGradient


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Text-to-Speech
        TTS.init(this) {
            TTS.speak("Welcome to Guide Me. Choose Search to look up how to do something, or go to the Lessons menu.")
        }

        // MainActivity.onCreate -> setContent
        setContent {
            GuideMeTheme {
                // Paint full-screen gradient first
                Box(Modifier.fillMaxSize().background(MainBackgroundGradient)) {
                    Scaffold(
                        containerColor = Color.Transparent,          // don't cover the gradient
                        contentWindowInsets = WindowInsets(0)        // no auto-padding; we’ll pass it manually
                    ) { innerPadding ->
                        MainScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
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
    // Screens: welcome -> main (lessons menu) -> phone/camera/wifi
    var currentScreen by remember { mutableStateOf("welcome") }

    when (currentScreen) {
        // ---------------- WELCOME ----------------
        "welcome" -> {
            WelcomeScreen(
                modifier = modifier.fillMaxSize(),
                onSearchClick = {
                    // Non-functional for now; just TTS
                    TTS.speak("Search will be available soon.")
                },
                onLessonsClick = {
                    TTS.speak("Opening lessons menu.")
                    currentScreen = "main"
                }
            )
            // If user presses back on Welcome, do nothing (let system handle exit)
        }

        // ---------------- LESSONS MENU (existing main) ----------------
        "main" -> {
            LessonsMenu(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                onOpenCamera = {
                    TTS.speak("Opening Camera.")
                    currentScreen = "camera"
                },
                onOpenPhone = {
                    TTS.speak("Opening Phone.")
                    currentScreen = "phone"
                },
                onOpenWifi = {
                    TTS.speak("Opening Wi-Fi.")
                    currentScreen = "wifi"
                }
            )
            BackHandler {
                TTS.speak("Returning to welcome.")
                currentScreen = "welcome"
            }
        }

        // ---------------- PHONE (bottom tabs) ----------------
        "phone" -> {
            PhoneNavHost()
            BackHandler {
                TTS.speak("Returning to lessons menu.")
                currentScreen = "main"
            }
        }

        // ---------------- CAMERA ----------------
        "camera" -> {
            CameraScreen()
            BackHandler {
                TTS.speak("Returning to lessons menu.")
                currentScreen = "main"
            }
        }

        // ---------------- WIFI ----------------
        "wifi" -> {
            WifiNavHost()
            BackHandler {
                TTS.speak("Returning to lessons menu.")
                currentScreen = "main"
            }
        }
    }
}

/* ------------ UI COMPOSABLES ------------ */


@Composable
private fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onLessonsClick: () -> Unit
) {
    // gradient brush (top to bottom)


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Welcome to GuideMe",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF6A4C93) // soft purple
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onLessonsClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(64.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5EFF7),
                    contentColor = Color(0xFF6A4C93)
                )
            ) {
                Text("Click here to learn")
            }

            Button(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(64.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5EFF7),
                    contentColor = Color(0xFF6A4C93)
                )
            ) {
                Text("Click here to search")
            }
        }
    }
}


@Composable
private fun LessonsMenu(
    modifier: Modifier = Modifier,
    onOpenCamera: () -> Unit,
    onOpenPhone: () -> Unit,
    onOpenWifi: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackgroundGradient)

    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "GuideMe Training Menu",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Button(onClick = onOpenCamera, modifier = Modifier.fillMaxWidth()) {
            Text("Camera")
        }

        Button(onClick = onOpenPhone, modifier = Modifier.fillMaxWidth()) {
            Text("Phone")
        }

        Button(onClick = onOpenWifi, modifier = Modifier.fillMaxWidth()) {
            Text("Wi-Fi")
        }
    }
}

@Preview(showBackground = true, name = "Welcome")
@Composable
fun PreviewWelcome() {
    GuideMeTheme {
        WelcomeScreen(
            onSearchClick = {},
            onLessonsClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Lessons Menu")
@Composable
fun PreviewLessonsMenu() {
    GuideMeTheme {
        LessonsMenu(
            onOpenCamera = {},
            onOpenPhone = {},
            onOpenWifi = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    GuideMeTheme { MainScreen() }
}


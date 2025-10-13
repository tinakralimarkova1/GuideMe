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
import com.example.guideme.phone.CameraScreen
import com.example.guideme.phone.PhoneScreen
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme
import com.example.guideme.wifi.WifiScreen

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
    var currentScreen by remember { mutableStateOf("main") }

    when (currentScreen) {
        "main" -> {
            Column(modifier = modifier.fillMaxSize()) {
                // Go to in-app Camera training screen
                Button(onClick = {
                    TTS.speak("Opening Camera.")
                    currentScreen = "camera"
                }) { Text("Camera") }

                // Go to in-app Phone training screen
                Button(onClick = {
                    TTS.speak("Opening Phone.")
                    currentScreen = "phone"
                }) { Text("Phone") }

                // Go to in-app Wi-Fi training screen
                Button(onClick = {
                    TTS.speak("Opening Wi-Fi.")
                    currentScreen = "wifi"
                }) { Text("Wi-Fi") }
            }
        }

        "phone" -> {
            PhoneScreen()
            BackHandler { currentScreen = "main" }
        }

        "camera" -> {
            CameraScreen()
            BackHandler { currentScreen = "main" }
        }

        "wifi" -> {
            WifiScreen()
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

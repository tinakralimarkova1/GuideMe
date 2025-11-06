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
import androidx.compose.material3.MaterialTheme


import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import  com.example.guideme.ui.theme.MainButtonColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.style.TextAlign
import com.example.guideme.ui.theme.MainBackgroundGradient
import com.example.guideme.ui.theme.Transparent
import  com.example.guideme.ui.theme.MainButtonContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic




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
                        containerColor = Transparent,          // don't cover the gradient
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
                    TTS.speak("Opening search.");
                    currentScreen = "search"
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
        // ---------------- SEARCH ----------------
        "search" -> {
            SearchMenu(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                onVoiceSearch = {
                    TTS.speak("Voice search coming soon. Say your question after the beep.");
                    // TODO: navigate to VoiceSearch screen or launch speech recognizer
                },
                onTextSearch = {
                    TTS.speak("Opening text search.");
                    // TODO: navigate to TextSearch screen
                }
            )
            BackHandler {
                TTS.speak("Returning to welcome.");
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
                color = MainButtonContentColor
            )

            Spacer(Modifier.height(0.dp))

            Button(
                onClick = onLessonsClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(130.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                Text("Click here to learn",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(0.dp))



            Button(
                onClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(130.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                Text("Click here to search",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center)
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
            .background(MainBackgroundGradient),



    ){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(
            text = "Lesson Menu",
            color = MainButtonContentColor,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(bottom = 40.dp)
                .padding(top = 60.dp),

        )

        Button(onClick = onOpenCamera,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp).height(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainButtonColor,
                contentColor = MainButtonContentColor
            )
        ) {
            Text("Camera",
                color = MainButtonContentColor,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Button(onClick = onOpenPhone,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp).height(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainButtonColor,
                contentColor = MainButtonContentColor
            )
        ){
            Text("Phone",
                color =MainButtonContentColor,
                style = MaterialTheme.typography.labelSmall)

        }

        Button(onClick = onOpenWifi,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp).height(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainButtonColor,
                contentColor = MainButtonContentColor
            )
        ){
            Text("Wi-Fi",
                color = MainButtonContentColor,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}}

@Composable
private fun SearchMenu(
    modifier: Modifier = Modifier,
    onVoiceSearch: () -> Unit,
    onTextSearch: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Search",
                color = MainButtonContentColor,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(top = 60.dp, bottom = 40.dp)
            )

            // ---- Voice search button ----
            Button(
                onClick = onVoiceSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(130.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                // If you have the icon lib, this shows a mic. Otherwise it’ll just show the text.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Microphone",
                        modifier = Modifier.size(52.dp)
                    )
                    Text(
                        "Say your question",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ---- Text search button ----
            Button(
                onClick = onTextSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(130.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                Text(
                    "Type your question",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
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

@Preview(showBackground = true, name = "Search Menu")
@Composable
fun PreviewSearchMenu() {
    GuideMeTheme {
        SearchMenu(
            onVoiceSearch = {},
            onTextSearch = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    GuideMeTheme { MainScreen() }
}


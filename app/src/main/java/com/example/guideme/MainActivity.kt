package com.example.guideme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guideme.phone.CameraScreen
import com.example.guideme.phone.PhoneNavHost
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme
import com.example.guideme.ui.theme.MainBackgroundGradient
import com.example.guideme.ui.theme.MainButtonColor
import com.example.guideme.ui.theme.MainButtonContentColor
import com.example.guideme.ui.theme.Transparent
import com.example.guideme.wifi.WifiNavHost
import com.example.guideme.lessons.LessonHost


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Text-to-Speech
        TTS.init(this) {
            TTS.speak("Welcome to Guide Me. Choose Search to look up how to do something, or go to the Lessons menu.")
        }

        setContent {
            GuideMeTheme {
                // Global gradient behind Scaffold
                Box(Modifier.fillMaxSize().background(MainBackgroundGradient)) {
                    Scaffold(
                        containerColor = Transparent,
                        contentWindowInsets = WindowInsets(0)
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
    // Screens: welcome -> main (lessons menu) -> phone/camera/wifi/lesson_phone/search
    var currentScreen by remember { mutableStateOf("welcome") }

    when (currentScreen) {
        "welcome" -> {
            WelcomeScreen(
                modifier = modifier.fillMaxSize(),
                onSearchClick = {
                    TTS.speak("Opening search.")
                    currentScreen = "search"
                },
                onLessonsClick = {
                    TTS.speak("Opening lessons menu.")
                    currentScreen = "main"
                }
            )
        }

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
                },
                onStartPhoneLesson = {
                    TTS.speak("Starting Phone Lesson.")
                    currentScreen = "lesson_phone"
                }
            )
            BackHandler {
                TTS.speak("Returning to welcome.")
                currentScreen = "welcome"
            }
        }

        "search" -> {
            SearchMenu(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                onVoiceSearch = {
                    TTS.speak("Voice search coming soon. Say your question after the beep.")
                },
                onTextSearch = {
                    TTS.speak("Opening text search.")
                }
            )
            BackHandler {
                TTS.speak("Returning to welcome.")
                currentScreen = "welcome"
            }
        }

        "phone" -> {
            PhoneNavHost()
            BackHandler {
                TTS.speak("Returning to lessons menu.")
                currentScreen = "main"
            }
        }

        "camera" -> {
            CameraScreen()
            BackHandler {
                TTS.speak("Returning to lessons menu.")
                currentScreen = "main"
            }
        }

        "wifi" -> {
            WifiNavHost()
            BackHandler {
                TTS.speak("Returning to lessons menu.")
                currentScreen = "main"
            }
        }

        "lesson_phone" -> {
            LessonHost(appName = "Phone", lessonId = 1) // <-- simple placeholder below
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
    Box(
        modifier = modifier.fillMaxSize(),
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
                Text(
                    "Click here to learn",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

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
                Text(
                    "Click here to search",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LessonsMenu(
    modifier: Modifier = Modifier,
    onOpenCamera: () -> Unit,
    onOpenPhone: () -> Unit,
    onOpenWifi: () -> Unit,
    onStartPhoneLesson: () -> Unit,         // <-- lifted callback
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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

            Button(
                onClick = onOpenCamera,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                Text("Camera", color = MainButtonContentColor, style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = onOpenPhone,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                Text("Phone", color = MainButtonContentColor, style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = onOpenWifi,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                Text("Wi-Fi", color = MainButtonContentColor, style = MaterialTheme.typography.labelSmall)
            }

            // Start Phone Lesson
            Button(
                onClick = onStartPhoneLesson,  // <-- use the callback
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                Text("Phone Lesson 1", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

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
                modifier = Modifier.padding(top = 60.dp, bottom = 40.dp)
            )

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Mic, contentDescription = "Microphone", modifier = Modifier.size(52.dp))
                    Text("Say your question", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }

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
                Text("Type your question", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            }
        }
    }
}

/* --------- Placeholder so it compiles; replace with your real lesson host later --------- */


/* -------------------- Previews -------------------- */

@Preview(showBackground = true, name = "Welcome")
@Composable
fun PreviewWelcome() {
    GuideMeTheme {
        WelcomeScreen(onSearchClick = {}, onLessonsClick = {})
    }
}

@Preview(showBackground = true, name = "Lessons Menu")
@Composable
fun PreviewLessonsMenu() {
    GuideMeTheme {
        LessonsMenu(
            onOpenCamera = {},
            onOpenPhone = {},
            onOpenWifi = {},
            onStartPhoneLesson = {}
        )
    }
}

@Preview(showBackground = true, name = "Search Menu")
@Composable
fun PreviewSearchMenu() {
    GuideMeTheme {
        SearchMenu(onVoiceSearch = {}, onTextSearch = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    GuideMeTheme { MainScreen() }
}

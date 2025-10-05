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
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme
import com.example.guideme.camera.CameraActivity
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If this import/call is unresolved on your setup, you can safely delete this line
        enableEdgeToEdge()

        // Initialize TTS
        TTS.init(this) {
            TTS.speak("Welcome to Guide Me. Please choose Camera, Phone, or Wi-Fi.")
        }

        setContent {
            GuideMeTheme {
                Scaffold { inner ->
                    MainScreen(
                        modifier = Modifier
                            .padding(inner)
                            .fillMaxSize(),
                        // ✅ Navigation stays in the Activity (safe; non-composable)
                        onCameraClick = {
                            TTS.speak("Camera is selected. Opening the guidance screen.")
                            startActivity(Intent(this, CameraActivity::class.java))
                        },
                        onPhoneClick = {
                            // We just signal the composable to show the dialog for Phone
                        },
                        onWifiClick = {
                            // We just signal the composable to show the dialog for Wi-Fi
                        }
                    )
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
fun MainScreen(
    modifier: Modifier = Modifier,
    onCameraClick: () -> Unit,
    onPhoneClick: () -> Unit,
    onWifiClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Which dialog to show: "phone" or "wifi" (we launch Camera guidance directly)
    var showDialogFor by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.padding(24.dp)) {

        // CAMERA — launch guidance Activity via the lambda (no composable APIs here)
        Button(onClick = onCameraClick) {
            Text("Camera")
        }

        Spacer(Modifier.height(12.dp))

        // PHONE — ask whether to open or guide
        Button(onClick = {
            TTS.speak("Phone is selected. Would you like me to open it for you, or guide you there?")
            showDialogFor = "phone"
            onPhoneClick() // optional: no-op hook to keep signature symmetric
        }) {
            Text("Phone")
        }

        Spacer(Modifier.height(12.dp))

        // WI-FI — ask whether to open or guide
        Button(onClick = {
            TTS.speak("Wi-Fi is selected. Would you like me to open it for you, or guide you there?")
            showDialogFor = "wifi"
            onWifiClick() // optional: no-op hook to keep signature symmetric
        }) {
            Text("Wi-Fi")
        }
    }

    // Dialog for Phone / Wi-Fi actions (safe to use LocalContext inside composables)
    showDialogFor?.let { choice ->
        AlertDialog(
            onDismissRequest = { showDialogFor = null },
            title = { Text("${choice.replaceFirstChar { it.uppercase() }} Options") },
            text = { Text("Would you like me to open the $choice app for you, or guide you there?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialogFor = null
                    when (choice) {
                        "phone" -> {
                            TTS.speak("Opening the phone app.")
                            val intent = Intent(Intent.ACTION_DIAL)
                            context.startActivity(intent)
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

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    GuideMeTheme {
        MainScreen(
            onCameraClick = {},
            onPhoneClick = {},
            onWifiClick = {}
        )
    }
}

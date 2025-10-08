package com.example.guideme.wifi

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guideme.tts.TTS

@Composable
fun WifiScreen() {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    // Speak once when the screen opens
    LaunchedEffect(Unit) {
        TTS.speak(
            "To connect to Wi-Fi, enter the network name and password, then tap Open Wi-Fi Settings. " +
                    "I'll take you there and you can finish the connection."
        )
    }

    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Wi-Fi", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = ssid,
            onValueChange = {
                ssid = it
                error = null
            },
            label = { Text("Network name (SSID)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = error != null
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = {
            showPassword = !showPassword
            TTS.speak(if (showPassword) "Showing password." else "Hiding password.")
        }) {
            Text(if (showPassword) "Hide password" else "Show password")
        }

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (ssid.isBlank()) {
                    error = "Please enter the Wi-Fi network name."
                    TTS.speak("Please enter the Wi-Fi network name.")
                    return@Button
                }
                // Copy details so user can paste in Settings if needed
                clipboard.setText(AnnotatedString("SSID: $ssid\nPassword: $password"))
                TTS.speak("Opening Wi-Fi settings. I copied your network details to the clipboard.")
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Wi-Fi Settings")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                clipboard.setText(AnnotatedString("SSID: $ssid\nPassword: $password"))
                TTS.speak("Copied network name and password to clipboard.")
            }
        ) {
            Text("Copy details")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Note: For privacy and security, Android requires completing Wi-Fi connections in Settings. " +
                    "This screen helps you prepare and guides you there.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WifiScreenPreview() {
    MaterialTheme { WifiScreen() }
}

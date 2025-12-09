package com.example.guideme.wifi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.lessons.anchorId
import com.example.guideme.tts.TTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiConnectScreen(
    nav: NavController,
    prefillSsid: String = "",
    requiresPassword: Boolean = true,
    onAnchorTapped: (String) -> Unit = {},
    correctAnchor: String? = null,
    tappedIncorrectAnchor: String? = null,
    isAnchorAllowed: (String) -> Boolean = { true },
    onNumberCommitted: (String) -> Unit = {}
) {
    var ssid by remember { mutableStateOf(prefillSsid) }
    var password by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(prefillSsid) {
        if (prefillSsid.isNotBlank()) {
            TTS.speak("You selected $prefillSsid. Enter the password to practice connecting.")
        } else {
            TTS.speak("Enter a network name and password to practice connecting.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connect to Network") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = ssid, onValueChange = { ssid = it },
                label = { Text("Network name (SSID)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().anchorId("Wifi.Connect.SSID")
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                        newValue ->
                    password = newValue
                    onNumberCommitted(newValue)
                                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { show = !show }) { Text(if (show) "Hide" else "Show") }
                },

                modifier = Modifier.fillMaxWidth().anchorId("Wifi.Connect.Password")
            )
            Button(
                onClick = {
                    val anchor = "Wifi.Connect.Button"
                    onAnchorTapped(anchor)

                    // Let the VM gate whether this button is allowed at this step
                    if (!isAnchorAllowed(anchor)) {
                        return@Button
                    }

                    if (ssid.isBlank()) {
                        TTS.speak("Please enter the network name.")
                    } else if (requiresPassword && password.isBlank()) {
                        TTS.speak("Please enter the password.")
                    } else {
                        TTS.speak("Great. On a real phone, tap the network named $ssid and enter the password to connect.")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .anchorId("Wifi.Connect.Button")
            ) { Text("Connect") }

        }
    }
}

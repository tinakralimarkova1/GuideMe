package com.example.guideme.wifi

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiConnectScreen(nav: NavController, prefillSsid: String = "") {
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
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password") }, singleLine = true,
                visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { show = !show }) { Text(if (show) "Hide" else "Show") }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (ssid.isBlank()) {
                        TTS.speak("Please enter the network name.")
                    } else {
                        TTS.speak("Great. On a real phone, tap the network named $ssid and enter the password to connect.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Practice Connect") }
        }
    }
}

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
fun WifiAddNetworkScreen(nav: NavController) {
    var ssid by remember { mutableStateOf("") }
    var security by remember { mutableStateOf("WPA/WPA2/WPA3-Personal") }
    var password by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { TTS.speak("This screen shows how to add a hidden network.") }

    val secOptions = listOf("None", "WEP", "WPA/WPA2/WPA3-Personal")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Network") },
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

            ExposedDropdownMenuBox(
                expanded = false, // static menu look for simplicity
                onExpandedChange = {}
            ) {
                OutlinedTextField(
                    value = security,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Security") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Tip instead of a live dropdown, to keep it simple
            AssistChip(onClick = { }, label = { Text("Options: ${secOptions.joinToString()}") })

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
                    if (ssid.isBlank()) TTS.speak("Please enter the network name.")
                    else TTS.speak("On a real phone, tap Save to add $ssid.")
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Practice Add") }
        }
    }
}

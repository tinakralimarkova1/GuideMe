package com.example.guideme.wifi

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiToggleScreen(nav: NavController) {
    var wifiOn by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { TTS.speak("This screen teaches how to turn Wi-Fi on or off.") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Turn Wi-Fi On/Off") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Use the switch to practice:")
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (wifiOn) "Wi-Fi is ON" else "Wi-Fi is OFF")
                Switch(
                    checked = wifiOn,
                    onCheckedChange = {
                        wifiOn = it
                        TTS.speak(if (it) "Wi-Fi on." else "Wi-Fi off.")
                    }
                )
            }
            Text(
                "Tip: On a real phone, open Settings → Network & Internet → Wi-Fi, then use the same switch."
            )
        }
    }
}

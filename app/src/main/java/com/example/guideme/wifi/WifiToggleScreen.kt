package com.example.guideme.wifi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiToggleScreen(nav: NavController,
                     onToggle: (String, Boolean) -> Unit) {
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
                        onToggle("Wifi.OnOffButton", it)
                    }
                )
            }
            Text(
                "Tip: On a real phone, open Settings → Network & Internet → Wi-Fi, then use the same switch."
            )
        }
    }
}

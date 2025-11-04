package com.example.guideme.wifi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS

data class FakeWifi(val ssid: String, val secured: Boolean, val strength: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiHomeScreen(nav: NavController) {
    var wifiOn by remember { mutableStateOf(true) }

    val networks = remember {
        listOf(
            FakeWifi("Campus-Guest", false, 3),
            FakeWifi("Home-5G", true, 4),
            FakeWifi("Coffee_Shop_WiFi", true, 2),
            FakeWifi("PrinterSetup", true, 1)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wi-Fi", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = {
                        TTS.speak("Returning to previous screen.")
                        nav.popBackStack()
                    }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = {
                        TTS.speak("Open Wi-Fi tips.")
                        nav.navigate("tips")
                    }) { Icon(Icons.Default.Settings, contentDescription = "Tips") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Toggle row (replicates Settings top area)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Use Wi-Fi", style = MaterialTheme.typography.titleMedium)
                            Text(
                                if (wifiOn) "On" else "Off",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Switch(
                        checked = wifiOn,
                        onCheckedChange = {
                            wifiOn = it
                            TTS.speak(if (it) "Wi-Fi turned on." else "Wi-Fi turned off.")
                        }
                    )
                }
            }

            // Networks list
            Text("Available networks", style = MaterialTheme.typography.titleSmall)
            if (!wifiOn) {
                Text("Turn Wi-Fi on to see networks.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(networks) { n ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    TTS.speak("Opening connect screen for ${n.ssid}.")
                                    val encoded = java.net.URLEncoder.encode(n.ssid, "UTF-8")
                                    nav.navigate("connect?ssid=$encoded")
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(n.ssid, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        if (n.secured) "Secured" else "Open network",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                StrengthPips(level = n.strength)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StrengthPips(level: Int) {
    // simple 4-bar strength indicator
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(4) { idx ->
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(((idx + 1) * 6).dp)
                    .background(
                        if (idx < level) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

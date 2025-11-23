package com.example.guideme.wifi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.lessons.anchorId
import com.example.guideme.lessons.flash
import com.example.guideme.tts.TTS

data class FakeWifi(val ssid: String, val secured: Boolean, val strength: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiHomeScreen(nav: NavController,
                   onButtonPressed: (String) -> Unit = {},
                   onTogglePressed: (String, Boolean) -> Unit = {_,_ ->},
                   correctAnchor: String? = null,
                   tappedIncorrectAnchor: String? = null,
                   isAnchorAllowed: (String) -> Boolean = { true },
                   initialWifiOn: Boolean? = null



) {
    //can change back later to true !!!!!!!!!!
    var wifiOn by remember(initialWifiOn) {
        mutableStateOf(initialWifiOn ?: true)
    }

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
                title = { Text("", fontWeight = FontWeight.SemiBold) },

            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .anchorId("Wifi.NetworkBox"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Toggle row (replicates Settings top area)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .anchorId("Wifi.OnOffRow")
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
                        modifier = Modifier
                            .anchorId("Wifi.WifiToggle")
                            .flash(tappedIncorrectAnchor,"Wifi.WifiToggle"),
                        checked = wifiOn,
                        onCheckedChange = {
                            wifiOn = it
                            TTS.speak(if (it) "Wi-Fi turned on." else "Wi-Fi turned off.")
                            onTogglePressed("Wifi.WifiToggle", it)
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
                        val networkAnchorId = "Wifi.Network.${n.ssid}"
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .anchorId(networkAnchorId)
                                .flash(tappedIncorrectAnchor, networkAnchorId)
                                .clickable {
                                    if (!isAnchorAllowed(networkAnchorId)) {
                                        // Wrong step: report tap only
                                        onButtonPressed(networkAnchorId)
                                    } else {
                                        //Correct step: report + navigate
                                        onButtonPressed(networkAnchorId)
                                        TTS.speak("Opening connect screen for ${n.ssid}.")
                                        val encoded = java.net.URLEncoder.encode(n.ssid, "UTF-8")
                                        nav.navigate("connect?ssid=$encoded")
                                    }
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
                                    Text(n.ssid, style = MaterialTheme.typography.titleMedium, modifier = Modifier.anchorId("Wifi.NetworkName.${n.ssid}"))
                                    Text(
                                        if (n.secured) "Secured" else "Open network",
                                        style = MaterialTheme.typography.bodySmall,

                                    )
                                }
                                Box(modifier = Modifier.anchorId("Wifi.Bars.${n.ssid}").padding()){
                                    StrengthPips(level = n.strength)
                                }

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
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WifiHomeScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    MaterialTheme {
        WifiHomeScreen(nav = navController, onButtonPressed = {},
            onTogglePressed = { _, _ -> })
    }
}
package com.example.guideme.wifi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.guideme.tts.TTS


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiNavHost(
    onAnchorTapped: (String) -> Unit = {},
    onToggle: (String, Boolean) -> Unit = {_,_ ->},
    correctAnchor: String? = null,
    tappedIncorrectAnchor: String? = null,
    isAnchorAllowed: (String) -> Boolean = { true },
    defaultStates: Map<String, String> = emptyMap(),
    onNumberCommitted: (String) -> Unit = {},
) {
    val nav = rememberNavController()
    var showIntro by remember { mutableStateOf(false) }

    val wifiOnDefault = defaultStates["Wifi.OnOffButton"]?.toBoolean()

    LaunchedEffect(Unit) {
        TTS.speak("You are now in Wi-Fi. This screen looks like your phone's Wi-Fi settings.")
    }

    if (showIntro) {
        AlertDialog(
            onDismissRequest = { showIntro = false },
            title = { Text("Choose an option", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            TTS.speak("You chose turn Wi-Fi on or off.")
                            showIntro = false
                            nav.navigate("toggle")
                        },
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                    ) { Text("Turn Wi-Fi On/Off") }

                    Button(
                        onClick = {
                            TTS.speak("You chose connect to a network.")
                            showIntro = false
                            nav.navigate("connect")
                        },
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                    ) { Text("Connect to Network") }

                    Button(
                        onClick = {
                            TTS.speak("You chose add network.")
                            showIntro = false
                            nav.navigate("add")
                        },
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                    ) { Text("Add Network") }

                    Button(
                        onClick = {
                            TTS.speak("You chose Wi-Fi tips.")
                            showIntro = false
                            nav.navigate("tips")
                        },
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                    ) { Text("Wi-Fi Tips") }
                }
            },
            confirmButton = {}
        )
    }

    NavHost(navController = nav, startDestination = "home") {
        composable("home") { WifiHomeScreen(
            nav = nav,
            onButtonPressed = { anchorId ->
                onAnchorTapped(anchorId)
            },
            onTogglePressed = { anchorId, value ->
                onToggle(anchorId, value)
            },
            correctAnchor = correctAnchor,
            tappedIncorrectAnchor = tappedIncorrectAnchor,
            isAnchorAllowed = isAnchorAllowed,
            initialWifiOn = wifiOnDefault

        )

        }
        composable("toggle") {
            WifiToggleScreen(
                nav = nav,
                onToggle = { anchorId, on ->
                    onToggle(anchorId, on)
                }
            )
        }
        composable("connect") {
            WifiConnectScreen(
                nav = nav,
                prefillSsid = "",
                requiresPassword = true,
                onAnchorTapped = { anchorId ->
                    onAnchorTapped(anchorId)
                },
                correctAnchor = correctAnchor,
                tappedIncorrectAnchor = tappedIncorrectAnchor,
                isAnchorAllowed = { anchorId ->
                    isAnchorAllowed(anchorId)
                },
                onNumberCommitted = { text ->
                    onNumberCommitted(text)
                }
            )
        }

        composable("add") { WifiAddNetworkScreen(nav) }
        composable("tips") { WifiTipsScreen(nav) }

        // optional: pass an SSID from the home list to connect
        composable(
            route = "connect?ssid={ssid}",
            arguments = listOf(navArgument("ssid") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStack ->
            val ssid = backStack.arguments?.getString("ssid") ?: ""
            WifiConnectScreen(
                nav = nav,
                prefillSsid = ssid,
                requiresPassword = true, // or look this up if you pass `secured`
                onAnchorTapped = { anchorId -> onAnchorTapped(anchorId) },
                correctAnchor = correctAnchor,
                tappedIncorrectAnchor = tappedIncorrectAnchor,
                isAnchorAllowed = { anchorId -> isAnchorAllowed(anchorId) },
                onNumberCommitted = { text -> onNumberCommitted(text) }
            )
        }
    }
}



package com.example.guideme.wifi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.guideme.tts.TTS
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiNavHost() {
    val nav = rememberNavController()
    var showIntro by remember { mutableStateOf(true) }

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
        composable("home") { WifiHomeScreen(nav) }
        composable("toggle") { WifiToggleScreen(nav) }
        composable("connect") { WifiConnectScreen(nav) }
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
            WifiConnectScreen(nav, ssid)
        }
    }
}



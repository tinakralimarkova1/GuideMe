package com.example.guideme.wifi

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS
import androidx.compose.runtime.LaunchedEffect


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiTipsScreen(nav: NavController) {
    LaunchedEffect(Unit) {
        TTS.speak("These are helpful Wi-Fi tips.")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wi-Fi Tips") },
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("• Stronger signal bars mean better connection.")
            Text("• Open networks don't need a password but are less secure.")
            Text("• If password fails, check uppercase/lowercase letters.")
            Text("• If stuck, turn Wi-Fi off and back on.")
        }
    }
}

package com.example.guideme.phone

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS

data class RecentCall(val name: String, val number: String, val whenLabel: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentsScreen(navController: NavController) {
    LaunchedEffect(Unit) { TTS.speak("You are now in recents.") }

    val recents = remember {
        listOf(
            RecentCall("TechBone", "5551112222", "Yesterday"),
            RecentCall("Private number", "5550000000", "Yesterday"),
            RecentCall("TechBone Vodafone", "5553334444", "Older"),
            RecentCall("TechBone Net", "5556667777", "Older")
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Recents", fontWeight = FontWeight.SemiBold) }) },
        bottomBar = { BottomNavBar(navController, "recents") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    TTS.speak("Opening dial pad.")
                    navController.navigate("dialpad")
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Text("⌨️") }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recents) { r ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            TTS.speak("Dialing ${r.name}.")
                            val encoded = Uri.encode(r.number)
                            navController.navigate("dialpad?number=$encoded")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(r.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("${r.whenLabel}  •  ${r.number}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

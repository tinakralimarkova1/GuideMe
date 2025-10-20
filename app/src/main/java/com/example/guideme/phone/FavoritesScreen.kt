package com.example.guideme.phone

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    LaunchedEffect(Unit) { TTS.speak("You are now in favorites.") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Favorites", fontWeight = FontWeight.SemiBold) }) },
        bottomBar = { BottomNavBar(navController, "favorites") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    TTS.speak("Opening dial pad.")
                    navController.navigate("dialpad")
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("⌨️")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("You have no favorite contacts yet.", style = MaterialTheme.typography.titleLarge)
        }
    }
}

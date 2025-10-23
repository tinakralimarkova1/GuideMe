package com.example.guideme.phone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    NavigationBar(tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = currentRoute == "favorites",
            onClick = {
                TTS.speak("You are entering favorites.")
                navController.navigate("favorites") {
                    popUpTo("favorites") { inclusive = false }
                }
            },
            icon = { Icon(Icons.Filled.Star, contentDescription = "Favorites") },
            label = { Text("Favorites") }
        )
        NavigationBarItem(
            selected = currentRoute == "recents",
            onClick = {
                TTS.speak("You are entering recents.")
                navController.navigate("recents") {
                    popUpTo("recents") { inclusive = false }
                }
            },
            icon = { Icon(Icons.Filled.Schedule, contentDescription = "Recents") },
            label = { Text("Recents") }
        )
        NavigationBarItem(
            selected = currentRoute == "contacts",
            onClick = {
                TTS.speak("You are entering contacts.")
                navController.navigate("contacts") {
                    popUpTo("contacts") { inclusive = false }
                }
            },
            icon = { Icon(Icons.Filled.Contacts, contentDescription = "Contacts") },
            label = { Text("Contacts") }
        )
    }
}

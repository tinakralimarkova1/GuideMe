package com.example.guideme.phone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.guideme.lessons.anchorId
import com.example.guideme.lessons.flash
import com.example.guideme.tts.TTS

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String,
    // 🔗 Lesson wiring
    onAnchorTapped: (String) -> Unit = {},
    tappedIncorrectAnchor: String? = null,
    correctAnchor: String? = null, // kept for future if you want special styling
    isAnchorAllowed: (String) -> Boolean = { true }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(min = 64.dp, max = 80.dp)
    ) {
        NavigationBar(
            tonalElevation = 8.dp,
            modifier = Modifier
                .padding(bottom = 0.dp, top = 4.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier.padding(top = 10.dp)
            ) {
                // ⭐ Favorites
                val favoritesAnchor = "Phone.BottomNav.Favorites"
                NavigationBarItem(
                    selected = currentRoute == "favorites",
                    onClick = {
                        if (!isAnchorAllowed(favoritesAnchor)) {
                            onAnchorTapped(favoritesAnchor)
                        } else {
                            onAnchorTapped(favoritesAnchor)
                            TTS.speak("You are entering favorites.")
                            navController.navigate("favorites") {
                                popUpTo("favorites") { inclusive = false }
                            }
                        }
                    },
                    icon = { Icon(Icons.Filled.Star, contentDescription = "Favorites") },
                    label = { Text("Favorites", fontSize = 17.sp) },
                    modifier = Modifier
                        .anchorId(favoritesAnchor)
                        .flash(tappedIncorrectAnchor, favoritesAnchor)
                )

                // ⏱ Recents
                val recentsAnchor = "Phone.BottomNav.Recents"
                NavigationBarItem(
                    selected = currentRoute == "recents",
                    onClick = {
                        if (!isAnchorAllowed(recentsAnchor)) {
                            onAnchorTapped(recentsAnchor)
                        } else {
                            onAnchorTapped(recentsAnchor)
                            TTS.speak("You are entering recents.")
                            navController.navigate("recents") {
                                popUpTo("recents") { inclusive = false }
                            }
                        }
                    },
                    icon = { Icon(Icons.Filled.Schedule, contentDescription = "Recents") },
                    label = { Text("Recents", fontSize = 17.sp) },
                    modifier = Modifier
                        .anchorId(recentsAnchor)
                        .flash(tappedIncorrectAnchor, recentsAnchor)
                )

                // 👤 Contacts
                val contactsAnchor = "Phone.BottomNav.Contacts"
                NavigationBarItem(
                    selected = currentRoute == "contacts",
                    onClick = {
                        if (!isAnchorAllowed(contactsAnchor)) {
                            onAnchorTapped(contactsAnchor)
                        } else {
                            onAnchorTapped(contactsAnchor)
                            TTS.speak("You are entering contacts.")
                            navController.navigate("contacts") {
                                popUpTo("contacts") { inclusive = false }
                            }
                        }
                    },
                    icon = { Icon(Icons.Filled.Contacts, contentDescription = "Contacts") },
                    label = { Text("Contacts", fontSize = 17.sp) },
                    modifier = Modifier
                        .anchorId(contactsAnchor)
                        .flash(tappedIncorrectAnchor, contactsAnchor)
                )
            }
        }
    }
}

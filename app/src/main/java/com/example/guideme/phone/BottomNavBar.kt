package com.example.guideme.phone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.guideme.tts.TTS


@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            //.heightIn(min= 80.dp, max =95.dp)
            //.padding(top=10.dp)// ⬅️ increase from 56dp → 64/72/80dp as needed
    ) {

        NavigationBar(
            tonalElevation = 8.dp,
            modifier = Modifier
                .padding(bottom = 0.dp, top = 4.dp)
                .clip(RoundedCornerShape(16.dp))
            //.heightIn(min = 50.dp, max = 80.dp)

        )


        {
            Row(modifier = Modifier.padding(top = 10.dp)

            ) {


                NavigationBarItem(
                    selected = currentRoute == "favorites",
                    onClick = {
                        TTS.speak("You are entering favorites.")
                        navController.navigate("favorites") {
                            popUpTo("favorites") { inclusive = false }
                        }
                    },
                    icon = { Icon(Icons.Filled.Star, contentDescription = "Favorites") },
                    label = { Text("Favorites", fontSize = 17.sp) }
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
                    label = { Text("Recents", fontSize = 17.sp) }
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
                    label = { Text("Contacts", fontSize = 17.sp) }
                )
            }
        }
    }

}

package com.example.guideme.phone

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNavHost(
    modifier: Modifier = Modifier,
    onAnchorTapped: (String) -> Unit = {},
    onNumberCommitted: (String) -> Unit = {},
    correctAnchor: String? = null,
    tappedIncorrectAnchor: String? = null

) {
    val navController = rememberNavController()
    var showIntroDialog by remember { mutableStateOf(true) }

//    LaunchedEffect(Unit) {
//        TTS.speak(
//            "You are now in phone. You can open favorites, look at your recent phone calls, " +
//                    "look through your contacts, or open the dial pad to call."
//        )
//    }

    //Might get rid of the inro dialogue if it interferes

//    if (showIntroDialog) {
//        AlertDialog(
//            onDismissRequest = { showIntroDialog = false },
//            title = { Text("Choose an option", style = MaterialTheme.typography.titleLarge) },
//            text = {
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//
//                ) {
//                    Button(
//                        onClick = {
//                            TTS.speak("You chose favorites.")
//                            showIntroDialog = false
//                            navController.navigate("favorites")
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    ) { Text("Favorites") }
//
//                    Button(
//                        onClick = {
//                            TTS.speak("You chose recents.")
//                            showIntroDialog = false
//                            navController.navigate("recents")
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    ) { Text("Recents") }
//
//                    Button(
//                        onClick = {
//                            TTS.speak("You chose contacts.")
//                            showIntroDialog = false
//                            navController.navigate("contacts")
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    ) { Text("Contacts") }
//
//                    Button(
//                        onClick = {
//                            TTS.speak("You chose call.")
//                            showIntroDialog = false
//                            navController.navigate("dialpad")
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    ) { Text("Dial Pad") }
//                }
//            },
//            confirmButton = {}
//        )
//    }

    NavHost(navController = navController, startDestination = "dialpad") {
        composable("favorites") { FavoritesScreen(navController) }
        composable("recents") { RecentsScreen(navController) }
        composable("contacts") { ContactsScreen(navController) }

        // dial pad can accept an optional number and auto-dial it
        composable(
            route = "dialpad?number={number}",
            arguments = listOf(navArgument("number") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {  backStackEntry ->
            val initialNumber = backStackEntry.arguments?.getString("number") ?: ""
            DialPadScreen(
                navController = navController,
                initialNumber = initialNumber,
                onButtonPressed = { anchorId ->
                    onAnchorTapped(anchorId)          // 🔁 bubble up to LessonHost
                },
                onNumberCommitted = { number ->
                    onNumberCommitted(number)         // 🔁 bubble up to LessonHost
                },
                correctAnchor = correctAnchor,
                tappedIncorrectAnchor = tappedIncorrectAnchor

            )
        }
    }
}

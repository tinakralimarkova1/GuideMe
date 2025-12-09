package com.example.guideme.phone

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.guideme.lessons.anchorId
import com.example.guideme.lessons.flash
import com.example.guideme.tts.TTS

data class Contact(val name: String, val phone: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    // 🔗 Lesson wiring
    onAnchorTapped: (String) -> Unit = {},
    onNumberCommitted:(String) -> Unit ={},
    correctAnchor: String? = null,
    tappedIncorrectAnchor: String? = null,
    isAnchorAllowed: (String) -> Boolean = { true }
) {
    LaunchedEffect(Unit) { TTS.speak("You are now in contacts.") }

    val contacts = remember {
        mutableStateListOf(
            Contact("Alice Johnson", "5551234567"),
            Contact("Bob Smith", "5559876543")
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone ->
                if (name.isBlank() || phone.isBlank()) {
                    TTS.speak("Please enter a name and a phone number.")
                } else {
                    contacts.add(Contact(name.trim(), phone.trim()))
                    TTS.speak("Contact added: $name.")
                    showAddDialog = false
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "",
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                },
                actions = {
                    val addAnchor = "Contacts.AddContact"
                    IconButton(
                        onClick = {
                            // gate for lesson
                            if (!isAnchorAllowed(addAnchor)) {
                                onAnchorTapped(addAnchor)
                            } else {
                                onAnchorTapped(addAnchor)
                                TTS.speak("Add new contact.")
                                showAddDialog = true
                            }
                        },
                        modifier = Modifier
                            .anchorId(addAnchor)
                            .flash(tappedIncorrectAnchor, addAnchor)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add contact")
                    }
                }
            )
        },
        // bottomBar is NOT used → we keep manual Row for nav bar at bottom
        floatingActionButton = {
            val fabAnchor = "Contacts.OpenDialPad"
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 310.dp), // pushes FAB up visually
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        if (!isAnchorAllowed(fabAnchor)) {
                            onAnchorTapped(fabAnchor)
                        } else {
                            onAnchorTapped(fabAnchor)
                            TTS.speak("Opening dial pad.")
                            navController.navigate("dialpad")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .anchorId(fabAnchor)
                        .flash(tappedIncorrectAnchor, fabAnchor)
                ) {
                    Text("⌨️")
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Main contacts content takes up available space above the bottom nav
            Box(
                modifier = Modifier
                    .weight(8f)
                    .fillMaxWidth()
                    // anchor for “this is the contacts list” highlight-only steps
                    .anchorId("Contacts.List")
            ) {
                if (contacts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No contacts yet. Tap + to add one.",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(contacts) { c ->
                            ContactRow(
                                c,
                                onClick = {
                                    TTS.speak("Dialing ${c.name}.")
                                    val encoded = Uri.encode(c.phone)
                                    navController.navigate("dialpad?number=$encoded")
                                }
                            )
                        }
                    }
                }
            }

            // Bottom nav bar anchored at the bottom in its own row,
            // leaving the usual empty band above for the lesson overlay.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 80.dp)
                    .weight(5f)
            ) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = "contacts",
                    onAnchorTapped = onAnchorTapped,
                    tappedIncorrectAnchor = tappedIncorrectAnchor,
                    correctAnchor = correctAnchor,
                    isAnchorAllowed = isAnchorAllowed
                )
            }
        }
    }
}

@Composable
private fun ContactRow(c: Contact, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .anchorId("Contacts") // temp change later
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                c.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(c.phone, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add contact", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone number") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onAdd(name, phone) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    TTS.speak("Canceled.")
                    onDismiss()
                }
            ) { Text("Cancel") }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContactsScreenPreview() {
    val nav = rememberNavController()
    MaterialTheme {
        ContactsScreen(navController = nav)
    }
}

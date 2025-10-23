package com.example.guideme.phone

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS

data class Contact(val name: String, val phone: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(navController: NavController) {
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
                title = { Text("Contacts", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = {
                        TTS.speak("Add new contact.")
                        showAddDialog = true
                    }) { Icon(Icons.Filled.Add, contentDescription = "Add contact") }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, "contacts") },
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
        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No contacts yet. Tap + to add one.", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
}

@Composable
private fun ContactRow(c: Contact, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(c.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone number") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { onAdd(name, phone) }) { Text("Add") } },
        dismissButton = { TextButton(onClick = { TTS.speak("Canceled."); onDismiss() }) { Text("Cancel") } }
    )
}

package com.example.guideme.phone

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.guideme.tts.TTS
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialPadScreen(
    navController: NavController,
    initialNumber: String = ""
) {
    var number by remember { mutableStateOf("") }
    var isCalling by remember { mutableStateOf(false) }

    // Prefill & auto-dial if number provided, else speak intro
    LaunchedEffect(initialNumber) {
        if (initialNumber.isNotBlank()) {
            number = initialNumber
            TTS.speak("Phone dialing.")
            isCalling = true
        } else {
            TTS.speak("You are now in the dial pad. You can type numbers and press call.")
        }
    }

    if (isCalling) {
        IncomingCallUI(
            number = number.ifEmpty { "Unknown" },
            onEndCall = {
                TTS.speak("Call ended.")
                number = ""
                isCalling = false
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dial Pad", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            TTS.speak("Returning to previous screen.")
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            },
            bottomBar = { BottomNavBar(navController, "dialpad") }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (number.isEmpty()) "Enter number" else number.chunked(3).joinToString(" "),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (number.isEmpty()) 0.35f else 1f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { if (number.isNotEmpty()) number = number.dropLast(1) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Delete"
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                DialPadKeys(
                    onKey = { key ->
                        if (number.length < 20) {
                            number += key
                            TTS.speak(
                                when (key) {
                                    "*" -> "star"
                                    "#" -> "pound"
                                    else -> key
                                }
                            )
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (number.isEmpty()) {
                            TTS.speak("Please enter a number.")
                        } else {
                            TTS.speak("Phone dialing.")
                            isCalling = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Call", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun IncomingCallUI(number: String, onEndCall: () -> Unit) {
    var isConnected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        TTS.speak("Incoming call from $number.")
        delay(2000)
        TTS.speak("Connected.")
        isConnected = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0B)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isConnected) "Call in progress..." else "Ringing...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )
            Text(
                text = number,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935))
                    .clickable { onEndCall() },
                contentAlignment = Alignment.Center
            ) {
                Text("End", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun DialPadKeys(onKey: (String) -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#"),
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { key ->
                    DialKey(label = key, modifier = Modifier.weight(1f)) { onKey(key) }
                }
            }
        }
    }
}

@Composable
private fun DialKey(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DialPadPreview() {
    MaterialTheme { }
}

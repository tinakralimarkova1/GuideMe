package com.example.guideme.phone

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guideme.tts.TTS

@Composable
fun PhoneScreen() {
    // Speak once when the screen opens
    LaunchedEffect(Unit) {
        TTS.speak("Enter a phone number or alias, then tap Dial.")
    }

    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Phone", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
                error = null
            },
            label = { Text("Number or alias (e.g., son)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 👈 here
            singleLine = true,
            isError = error != null,
            modifier = Modifier.fillMaxWidth()
        )


        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (input.isBlank()) {
                    error = "Please enter a number or alias."
                    TTS.speak("Please enter a number or alias.")
                } else {
                    TTS.speak("Dial placeholder. We will open the dialer next week.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dial")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneScreenPreview() {
    MaterialTheme { PhoneScreen() }
}

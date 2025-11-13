package com.example.guideme.lessons

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.guideme.util.HashUtils

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    customerDao: CustomerDao,
    onAuthSuccess: (DbCustomer) -> Unit
) {
    var isLogin by rememberSaveable { mutableStateOf(true) }

    var email by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var errorText by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    //TODO: fix UI so it fits with the rest
    Column(
        modifier = modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isLogin) "Login to GuideMe" else "Create a GuideMe account",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isLogin) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorText != null) {
            Text(
                text = errorText!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }


        Button(
            onClick = {
                scope.launch {
                    val trimmedEmail = email.trim()
                    val trimmedPassword = password.trim()

                    if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
                        errorText = "Email and password are required."
                        return@launch
                    }

                    if (isLogin) {
                        // LOGIN FLOW
                        val existing = customerDao.getCustomer(trimmedEmail)
                        if (existing == null) {
                            errorText = "Invalid email or password."
                        } else {
                            val hashedInput = HashUtils.hashPasswordWithSalt(trimmedPassword, existing.salt)
                            if (existing.password != hashedInput) {
                                errorText = "Invalid email or password."
                            } else {
                                errorText = null
                                onAuthSuccess(existing)
                            }
                        }
                    } else {
                        // REGISTER FLOW
                        if (name.isBlank()) {
                            errorText = "Name is required for registration."
                            return@launch
                        }

                        val salt = HashUtils.generateSalt()
                        val hashedPassword = HashUtils.hashPasswordWithSalt(trimmedPassword, salt)

                        val newCustomer = DbCustomer(
                            email = trimmedEmail,
                            name = name.trim(),
                            password = hashedPassword,
                            salt = salt
                        )

                        customerDao.insertCustomer(newCustomer)
                        errorText = null
                        onAuthSuccess(newCustomer)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "Login" else "Register")
        }

        TextButton(
            onClick = { isLogin = !isLogin }
        ) {
            Text(
                if (isLogin)
                    "New here? Create an account"
                else
                    "Already have an account? Log in"
            )
        }
    }
}

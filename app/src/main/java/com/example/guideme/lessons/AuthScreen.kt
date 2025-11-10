package com.example.guideme.lessons

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

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
    var phone by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf("") }
    var street by rememberSaveable { mutableStateOf("") }
    var building by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }   // "YYYY-MM-DD"

    var errorText by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

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

        if (!isLogin) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone number (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Street (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = building,
                onValueChange = { building = it },
                label = { Text("Building / Apt (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Date of birth (YYYY-MM-DD, optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

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
                    if (email.isBlank() || password.isBlank()) {
                        errorText = "Email and password are required."
                        return@launch
                    }

                    if (isLogin) {
                        // LOGIN FLOW
                        val existing = customerDao.getCustomer(email)
                        if (existing == null || existing.password != password) {
                            errorText = "Invalid email or password."
                        } else {
                            errorText = null
                            onAuthSuccess(existing)
                        }
                    } else {
                        // REGISTER FLOW
                        if (name.isBlank()) {
                            errorText = "Name is required for registration."
                            return@launch
                        }

                        val newCustomer = DbCustomer(
                            email = email,
                            name = name,
                            password = password,
                            city = city.ifBlank { null },
                            street = street.ifBlank { null },
                            state = state.ifBlank { null },
                            buildingNumber = building.ifBlank { null },
                            phoneNum = phone.ifBlank { null },
                            dateOfBirth = dob.ifBlank { null }
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

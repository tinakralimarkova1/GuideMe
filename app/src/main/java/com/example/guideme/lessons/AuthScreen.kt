package com.example.guideme.lessons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guideme.ui.theme.MainText
import com.example.guideme.ui.theme.NoticeColor
import com.example.guideme.ui.theme.SecondaryText
import com.example.guideme.util.HashUtils
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

    var errorText by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .padding( horizontal = 24.dp)
            .padding(top = 300.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isLogin) "Login to GuideMe" else "Create a GuideMe account",
            style = MaterialTheme.typography.headlineSmall,
            color = MainText
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = MainText) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainText,
                unfocusedBorderColor = SecondaryText,
                cursorColor = MainText,
                focusedLabelColor =MainText
            )


        )

        if (!isLogin) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainText,
                    unfocusedBorderColor = SecondaryText,
                    cursorColor = MainText,
                    focusedLabelColor =MainText
                )
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = MainText) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainText,
                unfocusedBorderColor = SecondaryText,
                cursorColor = MainText,
                focusedLabelColor =MainText
            )
        )

        if (errorText != null) {
            Text(
                text = errorText!!,
                color = NoticeColor,
                style = MaterialTheme.typography.headlineSmall
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,      // ⬅️ background color
                contentColor = MainText            // ⬅️ text & icon color
            )


        ) {
            Text(if (isLogin) "Login" else "Register", color = MainText)
        }

        TextButton(
            onClick = { isLogin = !isLogin }
        ) {
            Text(
                if (isLogin)
                    "New here? Create an account"
                else
                    "Already have an account? Log in",
                color = MainText
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthScreenPreview() {
    // Mock DAO implementing all abstract methods of CustomerDao
    val fakeDao = object : CustomerDao {
        override suspend fun getCustomer(email: String): DbCustomer? = null

        override suspend fun insertCustomer(customer: DbCustomer) {}

        override suspend fun getAccountRows(email: String): List<AccountRow> = emptyList()
    }

    MaterialTheme {
        AuthScreen(
            customerDao = fakeDao,
            onAuthSuccess = {}
        )
    }
}


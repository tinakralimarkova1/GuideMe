package com.example.guideme.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object HashUtils {

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPasswordWithSalt(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val saltedPassword = password + salt
        val digest = md.digest(saltedPassword.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}

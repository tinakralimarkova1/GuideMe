package com.example.guideme.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey

// Customer table from your ERD.
@Entity(tableName = "Customer")
data class DbCustomer(
    @PrimaryKey val email: String,
    val name: String,
    val password: String,
    val salt: String //Salt meaning that when two users have similar password, the hashing is still different
)



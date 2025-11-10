package com.example.guideme.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey

// Customer table from your ERD.
@Entity(tableName = "Customer")
data class DbCustomer(
    @PrimaryKey val email: String,
    val name: String,
    val password: String,
    val city: String?,
    val street: String?,
    val state: String?,
    val buildingNumber: String?,
    val phoneNum: String?,
    val dateOfBirth: String?   // "YYYY-MM-DD"
)

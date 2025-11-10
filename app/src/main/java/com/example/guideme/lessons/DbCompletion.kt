package com.example.guideme.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey

// One row = one user's completion record for one lesson.
@Entity(tableName = "Completion")
data class DbCompletion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: String?,          // "YYYY-MM-DD"
    val status: String?,        // e.g. "Completed"
    val timeSpent: Int?,        // seconds
    val errorCount: Int?,
    val attempts: Int?,
    // For now you can hardcode a demo email until you wire login.
    val customerEmail: String,
    val lessonsId: Int
)

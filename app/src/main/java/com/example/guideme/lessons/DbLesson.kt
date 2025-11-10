package com.example.guideme.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey

// One row per lesson (e.g., Phone Basics, Wi-Fi Basics, etc.)
@Entity(tableName = "Lessons")
data class DbLesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val difficulty: Int
)

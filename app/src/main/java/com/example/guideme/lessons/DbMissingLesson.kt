package com.example.guideme.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey

// logs a user query when no lesson matches it
@Entity(tableName = "MissingLesson")
data class DbMissingLesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val queryText: String,         // what the user said or typed
    val timestamp: Long = System.currentTimeMillis()
)
package com.example.guideme.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey

// This represents ONE ROW in the Instruction table in SQLite.
@Entity(tableName = "Instruction")
data class DbInstruction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val lessonsId: Int,
    val stepNo: Int,
    val text: String,
    val anchorId: String?,
    val type: String,
    val outlineColor: Long?
)

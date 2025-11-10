package com.example.guideme.lessons

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents one prerequisite link: prereqId must be completed before lessonId.
@Entity(tableName = "PreReq")
data class DbPreReq(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val lessonId: Int,     // the main lesson
    val prereqId: Int,     // prerequisite lesson
    val priority: Int?     // smaller = earlier / more important
)

package com.example.guideme.lessons

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DefaultButton")
data class DbDefaultButton(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "Lessons_ID")
    val lessonsId: Int,

    @ColumnInfo(name = "Button_Name")
    val buttonName: String,

    @ColumnInfo(name = "State")
    val state: String
)

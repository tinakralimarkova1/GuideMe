package com.example.guideme.lessons

data class AccountRow(
    val lessonName: String,
    val status: String,
    val timeSpent: Long,
    val errorCount: Int,
    val attempts: Int,
    val unmetPrereqs: String?
)

//this is a view
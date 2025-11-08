package com.example.guideme.lessons

interface LessonsRepository {
    suspend fun getLessonInstructions(appName: String, lessonId: Int): List<Instruction>

    // Save or update completion info for a lesson.
    suspend fun saveCompletion(
        lessonId: Int,
        status: String,
        timeSpentSeconds: Int,
        errorCount: Int,
        attempts: Int,
        customerEmail: String = "demo@guideme.app"   // temp until real user
    )
}

package com.example.guideme.lessons

interface LessonsRepository {
    suspend fun getLessonInstructions(appName: String, lessonId: Int): List<Instruction>

    suspend fun getDefaultButtonStates(lessonId: Int): Map<String, String>

    // Save or update completion info for a lesson.
    suspend fun saveCompletion(
        lessonId: Int,
        status: String,
        timeSpentSeconds: Int,
        errorCount: Int,
        attempts: Int,
        customerEmail: String = "demo@guideme.app"   // temp until real user
    )

    //used for recommendations
    suspend fun hasCompletedLesson(
        lessonId: Int,
        customerEmail: String = "demo@guideme.app"
    ): Boolean

    suspend fun getLessonById(lessonId: Int): DbLesson?


}

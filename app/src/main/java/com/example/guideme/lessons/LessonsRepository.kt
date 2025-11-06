package com.example.guideme.lessons

interface LessonsRepository {
    suspend fun getLessonInstructions(appName: String, lessonId: Int): List<Instruction>
}

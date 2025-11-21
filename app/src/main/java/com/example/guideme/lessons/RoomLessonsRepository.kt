package com.example.guideme.lessons

// No extra imports needed because Instruction and StepType
// are in the same package (Models.kt), and LessonsRepository
// is also in this package.

class RoomLessonsRepository(
    private val instructionDao: InstructionDao,
    private val completionDao: CompletionDao,
    private val defaultButtonDao: DefaultButtonDao

) : LessonsRepository {

    // 1) Read instructions for a lesson from SQLite
    override suspend fun getLessonInstructions(
        appName: String,
        lessonId: Int
    ): List<Instruction> {
        // For now we ignore appName and just filter by lessonId.
        val dbList = instructionDao.getInstructionsForLesson(lessonId)

        return dbList.map { db ->
            Instruction(
                stepNo = db.stepNo,
                text = db.text,

                anchorId = db.anchorId,
                type = StepType.valueOf(db.type),   // "TapTarget" -> StepType.TapTarget
                outlineColor = db.outlineColor,
                expectedText = db.expectedText
            )
        }
    }

    override suspend fun getDefaultButtonStates(lessonId: Int): Map<String, String> {
        val rows = defaultButtonDao.getDefaultsForLesson(lessonId)
        return rows.associate { it.buttonName to it.state }
    }

    // 2) Save / update completion info in the Completion table
    override suspend fun saveCompletion(
        lessonId: Int,
        status: String,
        timeSpentSeconds: Int,
        errorCount: Int,
        attempts: Int,
        customerEmail: String
    ) {
        // First see if we already have a row for this user+lesson.
        val existing = completionDao.getCompletion(customerEmail, lessonId)

        val today = java.time.LocalDate.now().toString()  // "YYYY-MM-DD"

        if (existing == null) {
            completionDao.insertCompletion(
                DbCompletion(
                    date = today,
                    status = status,
                    timeSpent = timeSpentSeconds,
                    errorCount = errorCount,
                    attempts = attempts,
                    customerEmail = customerEmail,
                    lessonsId = lessonId
                )
            )
        } else {
            completionDao.updateCompletion(
                existing.copy(
                    date = today,
                    status = status,
                    timeSpent = timeSpentSeconds,
                    errorCount = errorCount,
                    attempts = attempts
                )
            )
        }
    }
}

package com.example.guideme.lessons

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        DbInstruction::class,
        DbCompletion::class,
        DbLesson::class,
        DbCustomer::class,
        DbPreReq::class,
        DbMissingLesson::class,
        DbDefaultButton::class
    ],
    version = 50,              // ⬅ bump version for new tables
    exportSchema = false
)
abstract class GuideMeDatabase : RoomDatabase() {

    abstract fun instructionDao(): InstructionDao
    abstract fun completionDao(): CompletionDao
    abstract fun lessonDao(): LessonDao

    // 🆕
    abstract fun customerDao(): CustomerDao
    abstract fun preReqDao(): PreReqDao
    abstract fun missingLessonDao(): MissingLessonDao

    abstract fun defaultButtonDao(): DefaultButtonDao
}

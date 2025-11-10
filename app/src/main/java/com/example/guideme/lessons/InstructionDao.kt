package com.example.guideme.lessons

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InstructionDao {

    // Get all instructions for a given lesson, ordered by step number
    @Query("""
        SELECT * FROM Instruction
        WHERE lessonsId = :lessonId
        ORDER BY stepNo
    """)
    suspend fun getInstructionsForLesson(lessonId: Int): List<DbInstruction>

    // Used later to seed or update the DB with instructions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(instructions: List<DbInstruction>)
}
//DAO is so the Room knows this is a data-access object and it will be what to be used when nexeding to update or change data
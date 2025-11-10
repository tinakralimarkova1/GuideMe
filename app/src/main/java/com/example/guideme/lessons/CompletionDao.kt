package com.example.guideme.lessons

import androidx.room.*

@Dao
interface CompletionDao {

    @Query("""
        SELECT * FROM Completion
        WHERE customerEmail = :email AND lessonsId = :lessonId
        LIMIT 1
    """)
    suspend fun getCompletion(email: String, lessonId: Int): DbCompletion?

    @Insert
    suspend fun insertCompletion(completion: DbCompletion): Long

    @Update
    suspend fun updateCompletion(completion: DbCompletion)
}

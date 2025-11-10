package com.example.guideme.lessons

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PreReqDao {

    @Query("""
        SELECT * FROM PreReq
        WHERE lessonId = :lessonId
        ORDER BY priority
    """)
    suspend fun getPrereqsForLesson(lessonId: Int): List<DbPreReq>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prereqs: List<DbPreReq>)
}

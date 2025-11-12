package com.example.guideme.lessons

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MissingLessonDao {

    @Insert
    suspend fun insertMissingLesson(request: DbMissingLesson)

    @Query("SELECT * FROM MissingLesson ORDER BY timestamp DESC")
    suspend fun getAllMissingLessons(): List<DbMissingLesson>
}
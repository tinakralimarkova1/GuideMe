package com.example.guideme.lessons

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MissingLessonDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMissingLesson(missingLesson: DbMissingLesson)

    @Query("SELECT * FROM MissingLesson ORDER BY timestamp DESC")
    suspend fun getAllMissingLessons(): List<DbMissingLesson>
}
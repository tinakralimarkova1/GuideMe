package com.example.guideme.lessons

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LessonDao {

    @Query("SELECT * FROM Lessons ORDER BY id")
    suspend fun getAllLessons(): List<DbLesson>

    @Query("SELECT * FROM Lessons WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: Int): DbLesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lessons: List<DbLesson>)
}

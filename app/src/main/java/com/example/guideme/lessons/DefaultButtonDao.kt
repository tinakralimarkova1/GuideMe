package com.example.guideme.lessons

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DefaultButtonDao {

    @Query("SELECT * FROM DefaultButton WHERE Lessons_ID = :lessonId")
    suspend fun getDefaultsForLesson(lessonId: Int): List<DbDefaultButton>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(buttons: List<DbDefaultButton>)
}

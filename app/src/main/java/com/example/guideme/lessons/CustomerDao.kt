package com.example.guideme.lessons

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomerDao {

    @Query("SELECT * FROM Customer WHERE email = :email LIMIT 1")
    suspend fun getCustomer(email: String): DbCustomer?

    @Query("""
    SELECT 
        L.name                            AS lessonName,
        COALESCE(C.status, 'Not Started') AS status,
        COALESCE(C.timeSpent, 0)          AS timeSpent,
        COALESCE(C.errorCount, 0)         AS errorCount,
        COALESCE(C.attempts, 0)           AS attempts
    FROM Lessons L
    LEFT JOIN Completion C
      ON C.lessonsId = L.id
     AND C.customerEmail = :email
    ORDER BY L.id
""")
    suspend fun getAccountRows(email: String): List<AccountRow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: DbCustomer)
}

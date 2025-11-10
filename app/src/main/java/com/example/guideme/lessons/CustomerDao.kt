package com.example.guideme.lessons

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomerDao {

    @Query("SELECT * FROM Customer WHERE email = :email LIMIT 1")
    suspend fun getCustomer(email: String): DbCustomer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: DbCustomer)
}

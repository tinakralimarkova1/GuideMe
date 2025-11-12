package com.example.guideme.lessons

import androidx.room.*
import com.example.guideme.lessons.AccountRow


@Dao
interface CompletionDao {

    @Query("""
        SELECT * FROM Completion
        WHERE customerEmail = :email AND lessonsId = :lessonId
        LIMIT 1
    """)
    suspend fun getCompletion(email: String, lessonId: Int): DbCompletion?

    @Query("""
  SELECT
      L.name                                AS lessonName,
      COALESCE(C.status, 'Not Started')     AS status,
      COALESCE(C.timeSpent, 0)              AS timeSpent,
      COALESCE(C.errorCount, 0)             AS errorCount,
      COALESCE(C.attempts, 0)               AS attempts,
      /* names of prereqs the user has NOT completed yet */
      COALESCE(
        GROUP_CONCAT(
          CASE
            WHEN C2.rowid IS NULL THEN L2.name
            ELSE NULL
          END, ', '
        )
      , '')                                 AS unmetPrereqs
  FROM Lessons L
  /* user’s own completion on this lesson */
  LEFT JOIN Completion C
         ON C.lessonsId = L.id
        AND C.customerEmail = :email
  /* prereq mapping */
  LEFT JOIN PreReq PR
         ON PR.lessonId = L.id
  LEFT JOIN Lessons L2
         ON L2.id = PR.prereqId
  /* completion of each prereq by this user, only if Completed */
  LEFT JOIN Completion C2
         ON C2.lessonsId = PR.prereqId
        AND C2.customerEmail = :email
        AND C2.status = 'Completed'
  GROUP BY L.id
  ORDER BY L.id
""")
    suspend fun getAccountRows(email: String): List<AccountRow>



    @Insert
    suspend fun insertCompletion(completion: DbCompletion): Long

    @Update
    suspend fun updateCompletion(completion: DbCompletion)
}

package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM course_progress")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ProgressEntity)

    @Query("SELECT * FROM course_progress WHERE lessonId = :lessonId")
    suspend fun getProgressForLesson(lessonId: String): ProgressEntity?
}

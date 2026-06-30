package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_progress")
data class ProgressEntity(
    @PrimaryKey val lessonId: String,
    val isCompleted: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)

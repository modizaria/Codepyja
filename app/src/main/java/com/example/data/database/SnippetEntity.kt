package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_snippets")
data class SnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val language: String, // "python", "javascript", "html"
    val code: String,
    val updatedAt: Long = System.currentTimeMillis()
)

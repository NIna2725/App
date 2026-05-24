package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "professors")
data class Professor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val course: String,
    val faculty: String,
    val avgRating: Double = 0.0,
    val commentsCount: Int = 0
)

@Entity(tableName = "recommendations")
data class Recommendation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val professorId: Int,
    val courseName: String,
    val rating: Int,
    val comment: String,
    val tags: String, // Comma separated tags e.g., "Explica claro,Es exigente"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "suggestions")
data class TeacherSuggestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val professorName: String,
    val course: String,
    val faculty: String,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

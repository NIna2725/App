package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfessorDao {

    @Query("SELECT COUNT(*) FROM professors")
    suspend fun getProfessorsCount(): Int

    @Query("SELECT * FROM recommendations WHERE professorId = :professorId")
    suspend fun getRecommendationsListForProfessor(professorId: Int): List<Recommendation>

    @Query("SELECT * FROM professors ORDER BY name ASC")
    fun getAllProfessors(): Flow<List<Professor>>

    @Query("SELECT * FROM professors WHERE id = :id LIMIT 1")
    fun getProfessorById(id: Int): Flow<Professor?>

    @Query("SELECT * FROM recommendations WHERE professorId = :professorId ORDER BY timestamp DESC")
    fun getRecommendationsForProfessor(professorId: Int): Flow<List<Recommendation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfessor(professor: Professor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(recommendation: Recommendation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(suggestion: TeacherSuggestion): Long

    @Update
    suspend fun updateProfessor(professor: Professor)

    @Query("SELECT * FROM suggestions ORDER BY timestamp DESC")
    fun getAllSuggestions(): Flow<List<TeacherSuggestion>>
}

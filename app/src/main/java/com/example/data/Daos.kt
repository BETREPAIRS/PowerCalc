package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Query("SELECT * FROM calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<CalculationRecord>>

    @Query("SELECT * FROM calculations WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteCalculations(): Flow<List<CalculationRecord>>

    @Query("SELECT * FROM calculations WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getCalculationsForProject(projectId: Long): Flow<List<CalculationRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(record: CalculationRecord): Long

    @Update
    suspend fun updateCalculation(record: CalculationRecord)

    @Delete
    suspend fun deleteCalculation(record: CalculationRecord)

    @Query("DELETE FROM calculations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE calculations SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY dateCreated DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)
}

@Dao
interface LoadCircuitDao {
    @Query("SELECT * FROM load_circuits WHERE projectId = :projectId ORDER BY id ASC")
    fun getCircuitsForProject(projectId: Long): Flow<List<LoadCircuit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircuit(circuit: LoadCircuit): Long

    @Delete
    suspend fun deleteCircuit(circuit: LoadCircuit)

    @Query("DELETE FROM load_circuits WHERE projectId = :projectId")
    suspend fun deleteCircuitsForProject(projectId: Long)
}

@Dao
interface FieldNoteDao {
    @Query("SELECT * FROM field_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<FieldNote>>

    @Query("SELECT * FROM field_notes WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getNotesForProject(projectId: Long): Flow<List<FieldNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: FieldNote): Long

    @Delete
    suspend fun deleteNote(note: FieldNote)
}

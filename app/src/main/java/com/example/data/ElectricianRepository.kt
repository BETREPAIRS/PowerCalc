package com.example.data

import kotlinx.coroutines.flow.Flow

class ElectricianRepository(private val db: AppDatabase) {
    // Calculations
    val allCalculations: Flow<List<CalculationRecord>> = db.calculationDao().getAllCalculations()
    val favoriteCalculations: Flow<List<CalculationRecord>> = db.calculationDao().getFavoriteCalculations()

    fun getCalculationsForProject(projectId: Long): Flow<List<CalculationRecord>> =
        db.calculationDao().getCalculationsForProject(projectId)

    suspend fun saveCalculation(record: CalculationRecord): Long =
        db.calculationDao().insertCalculation(record)

    suspend fun deleteCalculation(id: Long) =
        db.calculationDao().deleteById(id)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) =
        db.calculationDao().setFavorite(id, isFavorite)

    // Projects
    val allProjects: Flow<List<Project>> = db.projectDao().getAllProjects()

    suspend fun getProjectById(id: Long): Project? =
        db.projectDao().getProjectById(id)

    suspend fun saveProject(project: Project): Long =
        db.projectDao().insertProject(project)

    suspend fun deleteProject(project: Project) {
        db.loadCircuitDao().deleteCircuitsForProject(project.id)
        db.projectDao().deleteProject(project)
    }

    // Load Circuits
    fun getCircuitsForProject(projectId: Long): Flow<List<LoadCircuit>> =
        db.loadCircuitDao().getCircuitsForProject(projectId)

    suspend fun addCircuit(circuit: LoadCircuit): Long =
        db.loadCircuitDao().insertCircuit(circuit)

    suspend fun deleteCircuit(circuit: LoadCircuit) =
        db.loadCircuitDao().deleteCircuit(circuit)

    // Field Notes
    val allNotes: Flow<List<FieldNote>> = db.fieldNoteDao().getAllNotes()

    fun getNotesForProject(projectId: Long): Flow<List<FieldNote>> =
        db.fieldNoteDao().getNotesForProject(projectId)

    suspend fun saveNote(note: FieldNote): Long =
        db.fieldNoteDao().insertNote(note)

    suspend fun deleteNote(note: FieldNote) =
        db.fieldNoteDao().deleteNote(note)
}

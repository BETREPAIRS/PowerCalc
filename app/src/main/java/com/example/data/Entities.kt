package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class CalculationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calculatorId: String,
    val title: String,
    val inputSummary: String,
    val resultSummary: String,
    val formulaUsed: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val notes: String = "",
    val projectId: Long? = null
)

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val clientName: String = "",
    val location: String = "",
    val dateCreated: Long = System.currentTimeMillis(),
    val notes: String = "",
    val status: String = "In Progress", // In Progress, Completed, Planning
    val systemVoltage: Double = 230.0,
    val defaultStandard: String = "IEC" // IEC, NEC, BS
)

@Entity(tableName = "load_circuits")
data class LoadCircuit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val circuitName: String,
    val loadType: String = "Lighting", // Lighting, Power, Motor, HVAC, Other
    val phase: String = "L1", // L1, L2, L3
    val watts: Double,
    val voltage: Double = 230.0,
    val powerFactor: Double = 0.9,
    val breakerRatingA: Int = 16
)

@Entity(tableName = "field_notes")
data class FieldNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long? = null,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String = "General" // General, Defect, Measurement, Equipment
)

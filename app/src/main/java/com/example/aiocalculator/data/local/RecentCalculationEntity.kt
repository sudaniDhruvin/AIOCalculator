package com.example.aiocalculator.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "recent_calculations",
    indices = [Index(value = ["calculatorType"], unique = true)]
)
data class RecentCalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val calculatorId: String,
    val calculatorName: String,
    val calculatorType: String,
    val iconName: String,
    val color: String,
    val detailsRoute: String,
    val timestamp: Long = System.currentTimeMillis(),
    val calculationData: String? = null // JSON string to store calculation details
)


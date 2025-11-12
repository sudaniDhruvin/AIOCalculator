package com.belbytes.calculators.data

import com.belbytes.calculators.data.local.AppDatabase
import com.belbytes.calculators.data.local.RecentCalculationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class RecentCalculationRepository(private val database: AppDatabase) {
    
    private val dao = database.recentCalculationDao()
    
    fun getAllRecentCalculations(): Flow<List<RecentCalculation>> {
        return dao.getAllRecentCalculations().map { entities ->
            entities.map { it.toRecentCalculation() }
        }
    }
    
    fun getRecentCalculations(limit: Int = 10): Flow<List<RecentCalculation>> {
        return dao.getRecentCalculations(limit).map { entities ->
            entities.map { it.toRecentCalculation() }
        }
    }
    
    suspend fun insertRecentCalculation(
        calculatorId: String,
        calculatorName: String,
        calculatorType: String,
        iconName: String,
        color: String,
        detailsRoute: String,
        calculationData: String? = null
    ): Long {
        // Use transaction to atomically delete old entry and insert new one
        // This ensures no duplicates can exist for the same main category
        val timestamp = System.currentTimeMillis()
        
        val result = dao.replaceCalculationByCalculatorType(
            calculatorId = calculatorId,
            calculatorName = calculatorName,
            calculatorType = calculatorType,
            iconName = iconName,
            color = color,
            detailsRoute = detailsRoute,
            timestamp = timestamp,
            calculationData = calculationData
        )
        
        // Clean up any remaining duplicates (safety measure)
        dao.removeDuplicateCalculations()
        
        return result
    }
    
    /**
     * Clean up duplicate calculations - removes all but the latest entry for each calculatorId
     */
    suspend fun cleanupDuplicates() {
        dao.removeDuplicateCalculations()
    }
    
    suspend fun deleteRecentCalculation(id: Long) {
        dao.deleteRecentCalculationById(id)
    }
    
    suspend fun deleteAllRecentCalculations() {
        dao.deleteAllRecentCalculations()
    }
    
    suspend fun getCount(): Int {
        return dao.getCount()
    }
}

// Extension function to convert Entity to Domain Model
private fun RecentCalculationEntity.toRecentCalculation(): RecentCalculation {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(timestamp))
    
    return RecentCalculation(
        id = this.id.toString(),
        calculatorId = this.calculatorId,
        calculatorType = this.calculatorType,
        date = date,
        iconName = this.iconName,
        detailsRoute = this.detailsRoute,
        color = this.color
    )
}


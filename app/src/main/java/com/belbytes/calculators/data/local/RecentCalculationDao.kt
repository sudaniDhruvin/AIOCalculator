package com.belbytes.calculators.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentCalculationDao {
    
    @Query("""
        SELECT * FROM recent_calculations 
        WHERE (calculatorType, timestamp) IN (
            SELECT calculatorType, MAX(timestamp) 
            FROM recent_calculations 
            GROUP BY calculatorType
        )
        ORDER BY timestamp DESC
    """)
    fun getAllRecentCalculations(): Flow<List<RecentCalculationEntity>>
    
    @Query("""
        SELECT * FROM recent_calculations 
        WHERE (calculatorType, timestamp) IN (
            SELECT calculatorType, MAX(timestamp) 
            FROM recent_calculations 
            GROUP BY calculatorType
        )
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    fun getRecentCalculations(limit: Int): Flow<List<RecentCalculationEntity>>
    
    @Query("""
        DELETE FROM recent_calculations 
        WHERE id NOT IN (
            SELECT id FROM recent_calculations 
            WHERE (calculatorType, timestamp) IN (
                SELECT calculatorType, MAX(timestamp) 
                FROM recent_calculations 
                GROUP BY calculatorType
            )
        )
    """)
    suspend fun removeDuplicateCalculations()
    
    @Query("SELECT * FROM recent_calculations WHERE calculatorType = :calculatorType ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestByCalculatorType(calculatorType: String): RecentCalculationEntity?
    
    @Query("DELETE FROM recent_calculations WHERE calculatorType = :calculatorType")
    suspend fun deleteByCalculatorType(calculatorType: String)
    
    @Transaction
    suspend fun replaceCalculationByCalculatorType(
        calculatorId: String,
        calculatorName: String,
        calculatorType: String,
        iconName: String,
        color: String,
        detailsRoute: String,
        timestamp: Long,
        calculationData: String?
    ): Long {
        // Delete all existing entries with the same calculatorType (main category)
        deleteByCalculatorType(calculatorType)
        
        // Insert new entry
        val entity = RecentCalculationEntity(
            calculatorId = calculatorId,
            calculatorName = calculatorName,
            calculatorType = calculatorType,
            iconName = iconName,
            color = color,
            detailsRoute = detailsRoute,
            timestamp = timestamp,
            calculationData = calculationData
        )
        return insertRecentCalculation(entity)
    }
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentCalculation(calculation: RecentCalculationEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentCalculations(calculations: List<RecentCalculationEntity>)
    
    @Delete
    suspend fun deleteRecentCalculation(calculation: RecentCalculationEntity)
    
    @Query("DELETE FROM recent_calculations WHERE id = :id")
    suspend fun deleteRecentCalculationById(id: Long)
    
    @Query("DELETE FROM recent_calculations")
    suspend fun deleteAllRecentCalculations()
    
    @Query("SELECT COUNT(*) FROM recent_calculations")
    suspend fun getCount(): Int
}


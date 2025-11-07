package com.example.aiocalculator.data

import android.content.Context
import com.example.aiocalculator.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility class to track and save calculator usage
 */
object CalculationTracker {
    
    /**
     * Save a calculation to recent calculations
     * 
     * @param context Application context
     * @param calculatorId Unique ID of the calculator
     * @param calculatorName Display name of the calculator
     * @param calculatorType Type/category of the calculator (e.g., "EMI Calculators")
     * @param iconName Icon resource name
     * @param color Color hex string
     * @param detailsRoute Route to navigate to calculation details
     * @param calculationData Optional JSON string with calculation details
     */
    fun saveCalculation(
        context: Context,
        calculatorId: String,
        calculatorName: String,
        calculatorType: String,
        iconName: String,
        color: String,
        detailsRoute: String,
        calculationData: String? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val repository = RecentCalculationRepository(database)
                
                repository.insertRecentCalculation(
                    calculatorId = calculatorId,
                    calculatorName = calculatorName,
                    calculatorType = calculatorType,
                    iconName = iconName,
                    color = color,
                    detailsRoute = detailsRoute,
                    calculationData = calculationData
                )
            } catch (e: Exception) {
                // Log error but don't crash
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Save calculation from CalculatorItemData
     */
    fun saveCalculationFromItem(
        context: Context,
        calculatorItem: CalculatorItemData,
        calculatorType: String,
        detailsRoute: String,
        calculationData: String? = null
    ) {
        saveCalculation(
            context = context,
            calculatorId = calculatorItem.id,
            calculatorName = calculatorItem.name,
            calculatorType = calculatorType,
            iconName = calculatorItem.iconName,
            color = calculatorItem.color,
            detailsRoute = detailsRoute,
            calculationData = calculationData
        )
    }
    
    /**
     * Save calculation by calculator ID - loads data from repository
     */
    fun saveCalculationForId(
        context: Context,
        calculatorId: String,
        calculatorType: String,
        route: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load app data to get calculator details
                val appData = DataRepository.loadAppData(context)
                val calculatorItem = appData.featuredTools
                    .flatMap { it.calculatorItems }
                    .find { it.id == calculatorId }
                
                if (calculatorItem != null) {
                    // Get the featured tool to get the category icon name
                    val featuredTool = appData.featuredTools.find { it.route == route }
                    val categoryIconName = featuredTool?.iconName ?: "ic_other_calculators"
                    
                    // Use category icon name instead of calculator item icon name
                    saveCalculation(
                        context = context,
                        calculatorId = calculatorItem.id,
                        calculatorName = calculatorItem.name,
                        calculatorType = calculatorType,
                        iconName = categoryIconName, // Use category icon
                        color = featuredTool?.color ?: calculatorItem.color,
                        detailsRoute = "$route/details/$calculatorId"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Save calculation when calculator is clicked from CalculatorsScreen
     * Finds the calculator's category automatically
     */
    fun saveCalculationFromCalculatorsScreen(
        context: Context,
        calculatorId: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load app data to get calculator details
                val appData = DataRepository.loadAppData(context)
                
                // Find which category this calculator belongs to
                var calculatorItem: CalculatorItemData? = null
                var featuredTool: FeaturedTool? = null
                
                for (tool in appData.featuredTools) {
                    calculatorItem = tool.calculatorItems.find { it.id == calculatorId }
                    if (calculatorItem != null) {
                        featuredTool = tool
                        break
                    }
                }
                
                if (calculatorItem != null && featuredTool != null) {
                    // Use category icon name and color
                    saveCalculation(
                        context = context,
                        calculatorId = calculatorItem.id,
                        calculatorName = calculatorItem.name,
                        calculatorType = featuredTool.name,
                        iconName = featuredTool.iconName,
                        color = featuredTool.color,
                        detailsRoute = "${featuredTool.route}/details/$calculatorId"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


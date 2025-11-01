package com.example.aiocalculator.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataRepository {
    private var appData: AppData? = null
    
    suspend fun loadAppData(context: Context): AppData {
        return withContext(Dispatchers.IO) {
            if (appData == null) {
                val jsonString = context.assets.open("app_data.json")
                    .bufferedReader()
                    .use { it.readText() }
                
                val gson = Gson()
                appData = gson.fromJson(jsonString, AppData::class.java)
            }
            appData!!
        }
    }
    
    fun getFeaturedTools(): List<FeaturedTool> {
        return appData?.featuredTools ?: emptyList()
    }
    
    fun getRecentCalculations(): List<RecentCalculation> {
        return appData?.recentCalculations ?: emptyList()
    }
    
    fun getBottomNavItems(): List<BottomNavItem> {
        return appData?.bottomNavigationItems ?: emptyList()
    }
}

// Data classes for JSON parsing
data class FeaturedTool(
    val id: String,
    val name: String,
    @SerializedName("iconName") val iconName: String,
    val route: String,
    val color: String
)

data class RecentCalculation(
    val id: String,
    @SerializedName("calculatorType") val calculatorType: String,
    val date: String,
    @SerializedName("iconName") val iconName: String,
    @SerializedName("detailsRoute") val detailsRoute: String,
    val color: String
)

data class BottomNavItem(
    val id: String,
    val label: String,
    @SerializedName("iconName") val iconName: String,
    val route: String,
    val selected: Boolean
)

data class AppData(
    @SerializedName("featuredTools") val featuredTools: List<FeaturedTool>,
    @SerializedName("recentCalculations") val recentCalculations: List<RecentCalculation>,
    @SerializedName("bottomNavigationItems") val bottomNavigationItems: List<BottomNavItem>
)


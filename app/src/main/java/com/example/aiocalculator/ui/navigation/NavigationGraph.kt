package com.example.aiocalculator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aiocalculator.ui.calculators.CalculatorsScreen
import com.example.aiocalculator.ui.history.HistoryScreen
import com.example.aiocalculator.ui.home.HomeScreen
import com.example.aiocalculator.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calculators : Screen("calculators")
    object History : Screen("history")
    object Settings : Screen("settings")
}

@Composable
fun NavigationGraph(navController: NavHostController, startDestination: String = Screen.Home.route) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onFeaturedToolClick = { route ->
                    // Handle navigation to specific calculator
                },
                onRecentCalculationClick = { route ->
                    // Handle navigation to calculation details
                },
                onViewAllFeatured = {
                    navController.navigate(Screen.Calculators.route)
                },
                onViewAllRecent = {
                    navController.navigate(Screen.History.route)
                }
            )
        }
        
        composable(Screen.Calculators.route) {
            CalculatorsScreen()
        }
        
        composable(Screen.History.route) {
            HistoryScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}


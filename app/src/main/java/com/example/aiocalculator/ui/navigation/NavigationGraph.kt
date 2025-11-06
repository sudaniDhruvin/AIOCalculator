package com.example.aiocalculator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aiocalculator.ui.calculators.CalculatorsScreen
import com.example.aiocalculator.ui.calculators.CommonCalculatorCategoryScreen
import com.example.aiocalculator.ui.history.HistoryScreen
import com.example.aiocalculator.ui.home.HomeScreen
import com.example.aiocalculator.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calculators : Screen("calculators")
    object History : Screen("history")
    object Settings : Screen("settings")
    
    // Calculator category screens
    object EMICalculators : Screen("emi_calculator")
    object SIPCalculators : Screen("sip_calculator")
    object LoanCalculators : Screen("loan_calculator")
    object BankCalculators : Screen("bank_calculator")
    object GSTVATCalculators : Screen("gst_vat_calculator")
    object OtherCalculators : Screen("other_calculators")
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
                    // Navigate to appropriate calculator category screen
                    when (route) {
                        "/emi_calculator" -> navController.navigate(Screen.EMICalculators.route)
                        "/sip_calculator" -> navController.navigate(Screen.SIPCalculators.route)
                        "/loan_calculator" -> navController.navigate(Screen.LoanCalculators.route)
                        "/bank_calculator" -> navController.navigate(Screen.BankCalculators.route)
                        "/gst_vat_calculator" -> navController.navigate(Screen.GSTVATCalculators.route)
                        "/other_calculators" -> navController.navigate(Screen.OtherCalculators.route)
                    }
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
        
        // Calculator category screens - all use common screen component with JSON data
        composable(Screen.EMICalculators.route) {
            CommonCalculatorCategoryScreen(
                route = "/emi_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    // Handle individual calculator click
                }
            )
        }
        
        composable(Screen.SIPCalculators.route) {
            CommonCalculatorCategoryScreen(
                route = "/sip_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    // Handle individual calculator click
                }
            )
        }
        
        composable(Screen.LoanCalculators.route) {
            CommonCalculatorCategoryScreen(
                route = "/loan_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    // Handle individual calculator click
                }
            )
        }
        
        composable(Screen.BankCalculators.route) {
            CommonCalculatorCategoryScreen(
                route = "/bank_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    // Handle individual calculator click
                }
            )
        }
        
        composable(Screen.GSTVATCalculators.route) {
            CommonCalculatorCategoryScreen(
                route = "/gst_vat_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    // Handle individual calculator click
                }
            )
        }
        
        composable(Screen.OtherCalculators.route) {
            CommonCalculatorCategoryScreen(
                route = "/other_calculators",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    // Handle individual calculator click
                }
            )
        }
    }
}


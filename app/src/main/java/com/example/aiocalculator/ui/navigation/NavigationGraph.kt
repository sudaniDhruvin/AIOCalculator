package com.example.aiocalculator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aiocalculator.data.CalculationTracker
import com.example.aiocalculator.data.DataRepository
import com.example.aiocalculator.ui.calculators.CalculatorsScreen
import com.example.aiocalculator.ui.calculators.CommonCalculatorCategoryScreen
import com.example.aiocalculator.ui.emi.EMICalculatorScreen
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
    
    // Individual calculator screens
    object EMICalculator : Screen("emi_calculator_input") {
        fun createRoute() = route
    }
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
            val context = LocalContext.current
            CalculatorsScreen(
                onCalculatorClick = { calculatorId ->
                    saveCalculationFromCalculatorsScreen(context, calculatorId)
                    // TODO: Navigate to actual calculator screen
                }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        
        // Calculator category screens - all use common screen component with JSON data
        composable(Screen.EMICalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/emi_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "EMI Calculators", "/emi_calculator")
                    // Navigate to EMI Calculator screen when id is "1"
                    if (calculatorId == "1") {
                        navController.navigate(Screen.EMICalculator.createRoute())
                    }
                }
            )
        }
        
        // Individual calculator screens
        composable(Screen.EMICalculator.createRoute()) {
            EMICalculatorScreen(
                onBackClick = { navController.popBackStack() },
                onCalculateClick = {
                    // TODO: Navigate to results screen
                }
            )
        }
        
        composable(Screen.SIPCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/sip_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "SIP Calculators", "/sip_calculator")
                    // TODO: Navigate to actual calculator screen
                }
            )
        }
        
        composable(Screen.LoanCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/loan_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "Loan Calculators", "/loan_calculator")
                    // TODO: Navigate to actual calculator screen
                }
            )
        }
        
        composable(Screen.BankCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/bank_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "Bank Calculators", "/bank_calculator")
                    // TODO: Navigate to actual calculator screen
                }
            )
        }
        
        composable(Screen.GSTVATCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/gst_vat_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "GST & VAT", "/gst_vat_calculator")
                    // TODO: Navigate to actual calculator screen
                }
            )
        }
        
        composable(Screen.OtherCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/other_calculators",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "Other Calculators", "/other_calculators")
                    // TODO: Navigate to actual calculator screen
                }
            )
        }
    }
}

/**
 * Helper function to save calculation when calculator is clicked
 * This should be called from a non-composable context
 */
private fun saveCalculationForId(
    context: android.content.Context,
    calculatorId: String,
    calculatorType: String,
    route: String
) {
    CalculationTracker.saveCalculationForId(
        context = context,
        calculatorId = calculatorId,
        calculatorType = calculatorType,
        route = route
    )
}

/**
 * Helper function to save calculation when calculator is clicked from CalculatorsScreen
 * Finds the calculator's category and saves it
 */
private fun saveCalculationFromCalculatorsScreen(
    context: android.content.Context,
    calculatorId: String
) {
    CalculationTracker.saveCalculationFromCalculatorsScreen(
        context = context,
        calculatorId = calculatorId
    )
}


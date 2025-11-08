package com.example.aiocalculator.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aiocalculator.data.CalculationTracker
import com.example.aiocalculator.data.DataRepository
import com.example.aiocalculator.ui.calculators.CalculatorsScreen
import com.example.aiocalculator.ui.calculators.CommonCalculatorCategoryScreen
import com.example.aiocalculator.ui.emi.AdvanceEMICalculatorScreen
import com.example.aiocalculator.ui.emi.AdvanceEMIDetailsScreen
import com.example.aiocalculator.ui.emi.AdvanceEMIResult
import com.example.aiocalculator.ui.emi.CompareLoansScreen
import com.example.aiocalculator.ui.emi.CompareLoansTableScreen
import com.example.aiocalculator.ui.emi.EMICalculatorScreen
import com.example.aiocalculator.ui.emi.EMIDetailsScreen
import com.example.aiocalculator.ui.emi.EMIResult
import com.example.aiocalculator.ui.emi.LoanTableEntry
import com.example.aiocalculator.ui.emi.QuickCalculatorScreen
import com.example.aiocalculator.ui.gst.GSTCalculatorScreen
import com.example.aiocalculator.ui.gst.VATCalculatorScreen
import com.example.aiocalculator.ui.other.DiscountCalculatorScreen
import com.example.aiocalculator.ui.other.ChargingTimeCalculatorScreen
import com.example.aiocalculator.ui.other.CashNoteCounterScreen
import com.example.aiocalculator.ui.bank.PPFCalculatorScreen
import com.example.aiocalculator.ui.bank.RDCalculatorScreen
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
    
    object EMIDetails : Screen("emi_details") {
        fun createRoute() = route
    }
    
    object QuickCalculator : Screen("quick_calculator") {
        fun createRoute() = route
    }
    
    object AdvanceEMICalculator : Screen("advance_emi_calculator") {
        fun createRoute() = route
    }
    
    object AdvanceEMIDetails : Screen("advance_emi_details") {
        fun createRoute() = route
    }
    
    object CompareLoans : Screen("compare_loans") {
        fun createRoute() = route
    }
    
    object CompareLoansTable : Screen("compare_loans_table") {
        fun createRoute() = route
    }
    
    object GSTCalculator : Screen("gst_calculator") {
        fun createRoute() = route
    }
    
    object VATCalculator : Screen("vat_calculator") {
        fun createRoute() = route
    }
    
    object DiscountCalculator : Screen("discount_calculator") {
        fun createRoute() = route
    }
    
    object ChargingTimeCalculator : Screen("charging_time_calculator") {
        fun createRoute() = route
    }
    
    object CashNoteCounter : Screen("cash_note_counter") {
        fun createRoute() = route
    }
    
    object PPFCalculator : Screen("ppf_calculator") {
        fun createRoute() = route
    }
    
    object RDCalculator : Screen("rd_calculator") {
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
                    // Navigate to Quick Calculator screen when id is "2"
                    else if (calculatorId == "2") {
                        navController.navigate(Screen.QuickCalculator.createRoute())
                    }
                    // Navigate to Advance EMI Calculator screen when id is "3"
                    else if (calculatorId == "3") {
                        navController.navigate(Screen.AdvanceEMICalculator.createRoute())
                    }
                    // Navigate to Compare Loans screen when id is "4"
                    else if (calculatorId == "4") {
                        navController.navigate(Screen.CompareLoans.createRoute())
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
                },
                onViewDetails = { result, amount, interestRate ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("emiResult", result)
                    navController.currentBackStackEntry?.savedStateHandle?.set("amount", amount)
                    navController.currentBackStackEntry?.savedStateHandle?.set("interestRate", interestRate)
                    navController.navigate(Screen.EMIDetails.createRoute())
                }
            )
        }
        
        composable(Screen.EMIDetails.createRoute()) {
            val emiResult = navController.previousBackStackEntry?.savedStateHandle?.get<EMIResult>("emiResult")
            val amount = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("amount") ?: 0.0
            val interestRate = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("interestRate") ?: 0.0
            
            if (emiResult != null) {
                EMIDetailsScreen(
                    emiResult = emiResult,
                    amount = amount,
                    interestRate = interestRate,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        
        composable(Screen.QuickCalculator.createRoute()) {
            QuickCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AdvanceEMICalculator.createRoute()) {
            AdvanceEMICalculatorScreen(
                onBackClick = { navController.popBackStack() },
                onViewDetails = { result, amount, interestRate, interestType, emiType ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("advanceEMIResult", result)
                    navController.currentBackStackEntry?.savedStateHandle?.set("amount", amount)
                    navController.currentBackStackEntry?.savedStateHandle?.set("interestRate", interestRate)
                    navController.currentBackStackEntry?.savedStateHandle?.set("interestType", interestType)
                    navController.currentBackStackEntry?.savedStateHandle?.set("emiType", emiType)
                    navController.navigate(Screen.AdvanceEMIDetails.createRoute())
                }
            )
        }
        
        composable(Screen.AdvanceEMIDetails.createRoute()) {
            val advanceEMIResult = navController.previousBackStackEntry?.savedStateHandle?.get<AdvanceEMIResult>("advanceEMIResult")
            val amount = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("amount") ?: 0.0
            val interestRate = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("interestRate") ?: 0.0
            val interestType = navController.previousBackStackEntry?.savedStateHandle?.get<String>("interestType") ?: "Reducing"
            val emiType = navController.previousBackStackEntry?.savedStateHandle?.get<String>("emiType") ?: "EMI In Arrears"
            
            if (advanceEMIResult != null) {
                AdvanceEMIDetailsScreen(
                    emiResult = advanceEMIResult,
                    amount = amount,
                    interestRate = interestRate,
                    interestType = interestType,
                    emiType = emiType,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        
        composable(Screen.CompareLoans.createRoute()) {
            CompareLoansScreen(
                onBackClick = { navController.popBackStack() },
                onCompareMoreClick = { loans ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("loans", loans)
                    navController.navigate(Screen.CompareLoansTable.createRoute())
                }
            )
        }
        
        composable(Screen.CompareLoansTable.createRoute()) {
            val loans = navController.previousBackStackEntry?.savedStateHandle?.get<List<LoanTableEntry>>("loans") ?: emptyList()
            CompareLoansTableScreen(
                onBackClick = { navController.popBackStack() },
                initialLoans = loans
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
                    // Navigate to RD Calculator screen when id is "16"
                    if (calculatorId == "16") {
                        navController.navigate(Screen.RDCalculator.createRoute())
                    }
                    // Navigate to PPF Calculator screen when id is "17"
                    else if (calculatorId == "17") {
                        navController.navigate(Screen.PPFCalculator.createRoute())
                    }
                }
            )
        }
        
        composable(Screen.PPFCalculator.createRoute()) {
            PPFCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RDCalculator.createRoute()) {
            RDCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.GSTVATCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/gst_vat_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "GST & VAT", "/gst_vat_calculator")
                    // Navigate to GST Calculator screen when id is "19"
                    if (calculatorId == "19") {
                        navController.navigate(Screen.GSTCalculator.createRoute())
                    }
                    // Navigate to VAT Calculator screen when id is "20"
                    else if (calculatorId == "20") {
                        navController.navigate(Screen.VATCalculator.createRoute())
                    }
                }
            )
        }
        
        composable(Screen.GSTCalculator.createRoute()) {
            GSTCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.VATCalculator.createRoute()) {
            VATCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.OtherCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/other_calculators",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "Other Calculators", "/other_calculators")
                    // Navigate to Discount Calculator screen when id is "21"
                    if (calculatorId == "21") {
                        navController.navigate(Screen.DiscountCalculator.createRoute())
                    }
                    // Navigate to Cash Note Counter screen when id is "22"
                    else if (calculatorId == "22") {
                        navController.navigate(Screen.CashNoteCounter.createRoute())
                    }
                    // Navigate to Charging Time Calculator screen when id is "23"
                    else if (calculatorId == "23") {
                        navController.navigate(Screen.ChargingTimeCalculator.createRoute())
                    }
                }
            )
        }
        
        composable(Screen.DiscountCalculator.createRoute()) {
            DiscountCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ChargingTimeCalculator.createRoute()) {
            ChargingTimeCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.CashNoteCounter.createRoute()) {
            CashNoteCounterScreen(
                onBackClick = { navController.popBackStack() }
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


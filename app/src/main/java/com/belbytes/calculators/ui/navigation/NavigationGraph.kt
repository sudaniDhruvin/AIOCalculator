package com.belbytes.calculators.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.belbytes.calculators.data.CalculationTracker
import com.belbytes.calculators.data.DataRepository
import com.belbytes.calculators.ui.calculators.CalculatorsScreen
import com.belbytes.calculators.ui.calculators.CommonCalculatorCategoryScreen
import com.belbytes.calculators.ui.emi.AdvanceEMICalculatorScreen
import com.belbytes.calculators.ui.emi.AdvanceEMIDetailsScreen
import com.belbytes.calculators.ui.emi.AdvanceEMIResult
import com.belbytes.calculators.ui.emi.CompareLoansScreen
import com.belbytes.calculators.ui.emi.CompareLoansTableScreen
import com.belbytes.calculators.ui.emi.EMICalculatorScreen
import com.belbytes.calculators.ui.emi.EMIDetailsScreen
import com.belbytes.calculators.ui.emi.EMIResult
import com.belbytes.calculators.ui.emi.LoanTableEntry
import com.belbytes.calculators.ui.emi.QuickCalculatorScreen
import com.belbytes.calculators.ui.gst.GSTCalculatorScreen
import com.belbytes.calculators.ui.gst.VATCalculatorScreen
import com.belbytes.calculators.ui.other.DiscountCalculatorScreen
import com.belbytes.calculators.ui.other.ChargingTimeCalculatorScreen
import com.belbytes.calculators.ui.other.CashNoteCounterScreen
import com.belbytes.calculators.ui.bank.PPFCalculatorScreen
import com.belbytes.calculators.ui.bank.RDCalculatorScreen
import com.belbytes.calculators.ui.bank.FDCalculatorScreen
import com.belbytes.calculators.ui.loan.CheckEligibilityScreen
import com.belbytes.calculators.ui.loan.MoratoriumCalculatorScreen
import com.belbytes.calculators.ui.loan.PrePaymentROIChangeScreen
import com.belbytes.calculators.ui.bank.SimpleInterestCalculatorScreen
import com.belbytes.calculators.ui.sip.STPCalculatorScreen
import com.belbytes.calculators.ui.sip.SWPCalculatorScreen
import com.belbytes.calculators.ui.sip.CompareSIPScreen
import com.belbytes.calculators.ui.sip.AdvanceSIPCalculatorScreen
import com.belbytes.calculators.ui.sip.QuickSIPCalculatorScreen
import com.belbytes.calculators.ui.sip.SIPCalculatorScreen
import com.belbytes.calculators.ui.history.HistoryScreen
import com.belbytes.calculators.ui.home.HomeScreen
import com.belbytes.calculators.ui.settings.SettingsScreen

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
    
    object FDCalculator : Screen("fd_calculator") {
        fun createRoute() = route
    }
    
    object CheckEligibility : Screen("check_eligibility") {
        fun createRoute() = route
    }
    
    object MoratoriumCalculator : Screen("moratorium_calculator") {
        fun createRoute() = route
    }
    
    object SimpleInterestCalculator : Screen("simple_interest_calculator") {
        fun createRoute() = route
    }
    
    object PrePaymentROIChange : Screen("pre_payment_roi_change") {
        fun createRoute() = route
    }
    
    object STPCalculator : Screen("stp_calculator") {
        fun createRoute() = route
    }
    
    object SWPCalculator : Screen("swp_calculator") {
        fun createRoute() = route
    }
    
    object CompareSIP : Screen("compare_sip") {
        fun createRoute() = route
    }
    
    object AdvanceSIPCalculator : Screen("advance_sip_calculator") {
        fun createRoute() = route
    }
    
    object QuickSIPCalculator : Screen("quick_sip_calculator") {
        fun createRoute() = route
    }
    
    object SIPCalculator : Screen("sip_calculator_main") {
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
                    // Navigate to the appropriate calculator screen based on calculator ID
                    navigateToCalculatorScreen(navController, calculatorId)
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
                    navigateToCalculatorScreen(navController, calculatorId)
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
                    navigateToCalculatorScreen(navController, calculatorId)
                }
            )
        }
        
        composable(Screen.STPCalculator.createRoute()) {
            STPCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SWPCalculator.createRoute()) {
            SWPCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.CompareSIP.createRoute()) {
            CompareSIPScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AdvanceSIPCalculator.createRoute()) {
            AdvanceSIPCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.QuickSIPCalculator.createRoute()) {
            QuickSIPCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SIPCalculator.createRoute()) {
            SIPCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.LoanCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/loan_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "Loan Calculators", "/loan_calculator")
                    navigateToCalculatorScreen(navController, calculatorId)
                }
            )
        }
        
        composable(Screen.CheckEligibility.createRoute()) {
            CheckEligibilityScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.MoratoriumCalculator.createRoute()) {
            MoratoriumCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.PrePaymentROIChange.createRoute()) {
            PrePaymentROIChangeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.BankCalculators.route) {
            val context = LocalContext.current
            CommonCalculatorCategoryScreen(
                route = "/bank_calculator",
                onBackClick = { navController.popBackStack() },
                onCalculatorClick = { calculatorId ->
                    saveCalculationForId(context, calculatorId, "Bank Calculators", "/bank_calculator")
                    navigateToCalculatorScreen(navController, calculatorId)
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
        
        composable(Screen.FDCalculator.createRoute()) {
            FDCalculatorScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SimpleInterestCalculator.createRoute()) {
            SimpleInterestCalculatorScreen(
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
                    navigateToCalculatorScreen(navController, calculatorId)
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
                    navigateToCalculatorScreen(navController, calculatorId)
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

/**
 * Navigate to the appropriate calculator screen based on calculator ID
 */
private fun navigateToCalculatorScreen(
    navController: NavHostController,
    calculatorId: String
) {
    when (calculatorId) {
        // EMI Calculators
        "1" -> navController.navigate(Screen.EMICalculator.createRoute())
        "2" -> navController.navigate(Screen.QuickCalculator.createRoute())
        "3" -> navController.navigate(Screen.AdvanceEMICalculator.createRoute())
        "4" -> navController.navigate(Screen.CompareLoans.createRoute())
        
        // SIP Calculators
        "5" -> navController.navigate(Screen.SIPCalculator.createRoute())
        "6" -> navController.navigate(Screen.QuickSIPCalculator.createRoute())
        "7" -> navController.navigate(Screen.AdvanceSIPCalculator.createRoute())
        "8" -> navController.navigate(Screen.CompareSIP.createRoute())
        "9" -> navController.navigate(Screen.SWPCalculator.createRoute())
        "10" -> navController.navigate(Screen.STPCalculator.createRoute())
        
        // Loan Calculators
        "11" -> navController.navigate(Screen.PrePaymentROIChange.createRoute())
        "12" -> {
            // Loan Profile - Navigate to Loan Calculators category for now
            // TODO: Add Loan Profile screen if it exists
            navController.navigate(Screen.LoanCalculators.route)
        }
        "13" -> navController.navigate(Screen.MoratoriumCalculator.createRoute())
        "14" -> navController.navigate(Screen.CheckEligibility.createRoute())
        
        // Bank Calculators
        "15" -> navController.navigate(Screen.FDCalculator.createRoute())
        "16" -> navController.navigate(Screen.RDCalculator.createRoute())
        "17" -> navController.navigate(Screen.PPFCalculator.createRoute())
        "18" -> navController.navigate(Screen.SimpleInterestCalculator.createRoute())
        
        // GST & VAT Calculators
        "19" -> navController.navigate(Screen.GSTCalculator.createRoute())
        "20" -> navController.navigate(Screen.VATCalculator.createRoute())
        
        // Other Calculators
        "21" -> navController.navigate(Screen.DiscountCalculator.createRoute())
        "22" -> navController.navigate(Screen.CashNoteCounter.createRoute())
        "23" -> navController.navigate(Screen.ChargingTimeCalculator.createRoute())
        
        else -> {
            // Default: Navigate to Calculators screen if ID not found
            navController.navigate(Screen.Calculators.route)
        }
    }
}


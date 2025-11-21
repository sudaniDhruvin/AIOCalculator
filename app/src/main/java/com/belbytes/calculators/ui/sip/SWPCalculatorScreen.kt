package com.belbytes.calculators.ui.sip

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.belbytes.calculators.R
import com.belbytes.calculators.utils.formatCurrencyWithDecimal
import kotlin.math.max
import kotlin.math.pow

data class SWPResult(
    val estimatedReturns: Double,
    val totalWithdrawal: Double,
    val finalBalance: Double
)

@Composable
fun SWPCalculatorScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    
    // State variables
    var totalInvestment by rememberSaveable { mutableStateOf("") }
    var withdrawalPerMonth by rememberSaveable { mutableStateOf("") }
    var expectedReturnRate by rememberSaveable { mutableStateOf("") }
    var periodYears by rememberSaveable { mutableStateOf("") }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var swpResult by remember { mutableStateOf<SWPResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Scroll to end when results are shown
    LaunchedEffect(showResults) {
        if (showResults) {
            delay(100) // Small delay to ensure content is rendered
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        SWPCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Total Investment Input
            SWPInputField(
                label = stringResource(R.string.total_investment),
                placeholder = stringResource(R.string.placeholder_amount),
                value = totalInvestment,
                onValueChange = { totalInvestment = it }
            )

            // Withdrawal Per Month Input
            SWPInputField(
                label = stringResource(R.string.withdrawal_per_month),
                placeholder = stringResource(R.string.placeholder_amount),
                value = withdrawalPerMonth,
                onValueChange = { withdrawalPerMonth = it }
            )

            // Expected Return Rate Input
            SWPInputField(
                label = stringResource(R.string.expected_return_rate),
                placeholder = stringResource(R.string.placeholder_rate),
                value = expectedReturnRate,
                onValueChange = { expectedReturnRate = it }
            )

            // Period (Years) Input
            SWPInputField(
                label = stringResource(R.string.period_years),
                placeholder = stringResource(R.string.placeholder_period),
                value = periodYears,
                onValueChange = { periodYears = it }
            )

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calculate Button
                Button(
                    onClick = {
                        keyboardController?.hide() // Hide keyboard when calculate is clicked
                        errorMessage = null
                        val result = calculateSWP(
                            totalInvestment = totalInvestment,
                            withdrawalPerMonth = withdrawalPerMonth,
                            expectedReturnRate = expectedReturnRate,
                            periodYears = periodYears
                        )
                        if (result != null) {
                            swpResult = result
                            showResults = true
                            errorMessage = null
                        } else {
                            showResults = false
                            swpResult = null
                            errorMessage = when {
                                totalInvestment.isBlank() || (totalInvestment.toDoubleOrNull() ?: -1.0) <= 0 ->
                                    context.getString(R.string.error_valid_total_investment)
                                withdrawalPerMonth.isBlank() || (withdrawalPerMonth.toDoubleOrNull() ?: -1.0) <= 0 ->
                                    context.getString(R.string.error_valid_withdrawal_per_month)
                                expectedReturnRate.isBlank() || expectedReturnRate.toDoubleOrNull() == null ->
                                    context.getString(R.string.error_valid_expected_return_rate)
                                periodYears.isBlank() || (periodYears.toDoubleOrNull() ?: -1.0) <= 0 ->
                                    context.getString(R.string.error_invalid_period)
                                else -> {
                                    val investment = totalInvestment.toDoubleOrNull() ?: 0.0
                                    val withdrawal = withdrawalPerMonth.toDoubleOrNull() ?: 0.0
                                    val years = periodYears.toDoubleOrNull() ?: 0.0
                                    val totalWithdrawals = withdrawal * years * 12
                                    if (totalWithdrawals > investment * 2) {
                                        context.getString(R.string.withdrawal_amount_too_high)
                                    } else {
                                        context.getString(R.string.error_check_inputs)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.calculate),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Reset Button
                Button(
                    onClick = {
                        totalInvestment = ""
                        withdrawalPerMonth = ""
                        expectedReturnRate = ""
                        periodYears = ""
                        showResults = false
                        swpResult = null
                        errorMessage = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEBEBEB)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }

            // Error Message Section
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Results Section
            AnimatedVisibility(
                visible = showResults && swpResult != null,
                enter = expandVertically(
                    animationSpec = tween(300),
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    animationSpec = tween(300)
                ),
                exit = shrinkVertically(
                    animationSpec = tween(300),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(
                    animationSpec = tween(300)
                )
            ) {
                swpResult?.let { result ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Horizontal Divider
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Results Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF7F7F7)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SWPResultRow(
                                    label = stringResource(R.string.estimated_returns),
                                    value = formatCurrencyWithDecimal(context, result.estimatedReturns)
                                )
                                SWPResultRow(
                                    label = stringResource(R.string.total_withdrawal),
                                    value = formatCurrencyWithDecimal(context, result.totalWithdrawal)
                                )
                                SWPResultRow(
                                    label = stringResource(R.string.final_balance),
                                    value = formatCurrencyWithDecimal(context, result.finalBalance)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SWPCalculatorHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color(0xFF2196F3))
            .statusBarsPadding()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "SWP Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SWPInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
    }
}

@Composable
fun SWPResultRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

fun calculateSWP(
    totalInvestment: String,
    withdrawalPerMonth: String,
    expectedReturnRate: String,
    periodYears: String
): SWPResult? {
    return try {
        val initialInvestment = totalInvestment.toDoubleOrNull() ?: return null
        val monthlyWithdrawal = withdrawalPerMonth.toDoubleOrNull() ?: return null
        val annualRate = expectedReturnRate.toDoubleOrNull() ?: return null
        val years = periodYears.toDoubleOrNull() ?: return null
        
        if (initialInvestment <= 0 || monthlyWithdrawal <= 0 || years <= 0) return null
        
        // Step 1: Convert Annual Rate to Monthly Rate
        // Monthly Rate = Annual Rate / 12 / 100
        val r = (annualRate / 100) / 12
        
        // Step 2: Convert Years to Months
        val n = (years * 12).toInt()
        
        if (n <= 0) return null
        
        // Step 3: Calculate Final Balance
        // Final Balance = P × (1 + r)^n - W × ((1 + r)^n - 1) / r
        val finalBalance = if (r > 0) {
            val growthFactor = java.lang.Math.pow(1 + r, n.toDouble())
            val finalBalanceValue = initialInvestment * growthFactor - 
                                   (monthlyWithdrawal * (growthFactor - 1) / r)
            max(finalBalanceValue, 0.0) // Ensure balance doesn't go negative
        } else {
            // If rate is 0, simple calculation
            max(initialInvestment - (monthlyWithdrawal * n), 0.0)
        }
        
        // Step 4: Calculate Total Withdrawals
        // Total Withdrawals = Monthly Withdrawal × Years × 12
        val totalWithdrawals = monthlyWithdrawal * years * 12
        
        // Step 5: Calculate Estimated Returns
        // Estimated Returns = (Final Balance + Total Withdrawals) - Initial Investment
        val estimatedReturns = (finalBalance + totalWithdrawals) - initialInvestment
        
        SWPResult(
            estimatedReturns = estimatedReturns,
            totalWithdrawal = totalWithdrawals,
            finalBalance = finalBalance
        )
    } catch (e: Exception) {
        null
    }
}



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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

data class STPResult(
    val totalAmountTransferred: Double,
    val totalProfit: Double,
    val balanceInTransferor: Double,
    val balanceInTransferee: Double
)

enum class STPPeriodType {
    YEARS,
    MONTHS
}

@Composable
fun STPCalculatorScreen(
    onBackClick: () -> Unit
) {
    // State variables
    var investmentAmount by rememberSaveable { mutableStateOf("") }
    var stpAmount by rememberSaveable { mutableStateOf("") }
    var transferorRate by rememberSaveable { mutableStateOf("") }
    var transfereeRate by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("") }
    var periodTypeString by rememberSaveable { mutableStateOf(STPPeriodType.YEARS.name) }
    val periodType = remember(periodTypeString) {
        try {
            STPPeriodType.valueOf(periodTypeString)
        } catch (e: IllegalArgumentException) {
            STPPeriodType.YEARS
        }
    }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var stpResult by rememberSaveable { mutableStateOf<STPResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        STPCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Investment Amount Input
            STPInputField(
                label = "Investment Amount",
                placeholder = "Ex: 1,00,000",
                value = investmentAmount,
                onValueChange = { investmentAmount = it }
            )

            // STP Amount Input
            STPInputField(
                label = "STP Amount",
                placeholder = "Ex: 1000",
                value = stpAmount,
                onValueChange = { stpAmount = it }
            )

            // Transferor(%) Input
            STPInputField(
                label = "Transferor(%)",
                placeholder = "Ex: 12%",
                value = transferorRate,
                onValueChange = { transferorRate = it }
            )

            // Transferee(%) Input
            STPInputField(
                label = "Transferee(%)",
                placeholder = "Ex: 10",
                value = transfereeRate,
                onValueChange = { transfereeRate = it }
            )

            // Period Input
            STPInputField(
                label = "Period",
                placeholder = if (periodType == STPPeriodType.YEARS) "Ex: 6" else "Ex: 72",
                value = period,
                onValueChange = { period = it }
            )

            // Period Type Radio Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                STPPeriodTypeRadioButton(
                    label = "Years",
                    selected = periodType == STPPeriodType.YEARS,
                    onClick = { periodTypeString = STPPeriodType.YEARS.name }
                )
                STPPeriodTypeRadioButton(
                    label = "Months",
                    selected = periodType == STPPeriodType.MONTHS,
                    onClick = { periodTypeString = STPPeriodType.MONTHS.name }
                )
            }

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
                        errorMessage = null
                        val result = calculateSTP(
                            investmentAmount = investmentAmount,
                            stpAmount = stpAmount,
                            transferorRate = transferorRate,
                            transfereeRate = transfereeRate,
                            period = period,
                            periodType = periodType
                        )
                        if (result != null) {
                            stpResult = result
                            showResults = true
                            errorMessage = null
                        } else {
                            showResults = false
                            stpResult = null
                            errorMessage = when {
                                investmentAmount.isBlank() || investmentAmount.toDoubleOrNull() == null || investmentAmount.toDoubleOrNull()!! <= 0 ->
                                    "Please enter a valid investment amount"
                                stpAmount.isBlank() || stpAmount.toDoubleOrNull() == null || stpAmount.toDoubleOrNull()!! <= 0 ->
                                    "Please enter a valid STP amount"
                                transferorRate.isBlank() || transferorRate.toDoubleOrNull() == null || transferorRate.toDoubleOrNull()!! <= 0 ->
                                    "Please enter a valid transferor rate"
                                transfereeRate.isBlank() || transfereeRate.toDoubleOrNull() == null ->
                                    "Please enter a valid transferee rate"
                                period.isBlank() || period.toDoubleOrNull() == null || period.toDoubleOrNull()!! <= 0 ->
                                    "Please enter a valid period"
                                else -> {
                                    val invAmount = investmentAmount.toDoubleOrNull() ?: 0.0
                                    val stpAmt = stpAmount.toDoubleOrNull() ?: 0.0
                                    if (stpAmt >= invAmount) {
                                        "STP amount must be less than investment amount"
                                    } else {
                                        "Please check all input values"
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2079EC)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Calculate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Reset Button
                Button(
                    onClick = {
                        investmentAmount = ""
                        stpAmount = ""
                        transferorRate = ""
                        transfereeRate = ""
                        period = ""
                        periodTypeString = STPPeriodType.YEARS.name
                        showResults = false
                        stpResult = null
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
                        text = "Reset",
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
                visible = showResults && stpResult != null,
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
                stpResult?.let { result ->
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
                                STPResultRow(
                                    label = "Total Amount Transferred",
                                    value = formatCurrencyWithDecimal(result.totalAmountTransferred)
                                )
                                STPResultRow(
                                    label = "Total Profit",
                                    value = formatCurrencyWithDecimal(result.totalProfit)
                                )
                                STPResultRow(
                                    label = "Balance in Transferor",
                                    value = formatCurrencyWithDecimal(result.balanceInTransferor)
                                )
                                STPResultRow(
                                    label = "Balance in Transferee",
                                    value = formatCurrencyWithDecimal(result.balanceInTransferee)
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
fun STPCalculatorHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color(0xFF2079EC))
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
            text = "STP Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun STPInputField(
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
                    color = Color.Black,
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
            singleLine = true
        )
    }
}

@Composable
fun STPPeriodTypeRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF222222),
                unselectedColor = Color(0xFF757575)
            )
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun STPResultRow(
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

fun calculateSTP(
    investmentAmount: String,
    stpAmount: String,
    transferorRate: String,
    transfereeRate: String,
    period: String,
    periodType: STPPeriodType
): STPResult? {
    return try {
        val investment = investmentAmount.toDoubleOrNull() ?: return null
        val stpAmt = stpAmount.toDoubleOrNull() ?: return null
        val transferor = transferorRate.toDoubleOrNull() ?: return null
        val transferee = transfereeRate.toDoubleOrNull() ?: return null
        val periodValue = period.toDoubleOrNull() ?: return null
        
        if (investment <= 0 || stpAmt <= 0 || transferor <= 0 || periodValue <= 0) return null
        if (stpAmt >= investment) return null
        
        // Convert period to months
        val periodMonths = if (periodType == STPPeriodType.YEARS) {
            Math.round(periodValue * 12).toInt()
        } else {
            Math.round(periodValue).toInt()
        }
        
        if (periodMonths <= 0) return null
        
        // Calculate monthly rate for transferor and transferee
        val transferorMonthlyRate = transferor / (12 * 100)
        val transfereeMonthlyRate = transferee / (12 * 100)
        
        // Initialize balances
        var balanceInTransferor = investment
        var balanceInTransferee = 0.0
        var totalAmountTransferred = 0.0
        
        // Simulate monthly transfers for the full period
        // Following the exact order from documentation:
        // 1. Deduct STP amount from Transferor first
        // 2. Apply interest on remaining Transferor balance
        // 3. Add STP amount to Transferee
        // 4. Apply interest on updated Transferee balance
        for (month in 1..periodMonths) {
            // Step 1: Deduct STP amount from Transferor first
            balanceInTransferor -= stpAmt
            totalAmountTransferred += stpAmt
            
            // Step 2: Apply interest on the remaining Transferor balance
            // (even if balance is negative, interest is still applied)
            balanceInTransferor *= (1 + transferorMonthlyRate)
            
            // Step 3: Add STP amount to Transferee
            balanceInTransferee += stpAmt
            
            // Step 4: Apply interest on the updated Transferee balance
            balanceInTransferee *= (1 + transfereeMonthlyRate)
        }
        
        // Calculate total profit
        // Total Profit = (Balance in Transferor + Balance in Transferee) - Initial Investment
        val totalProfit = (balanceInTransferor + balanceInTransferee) - investment
        
        STPResult(
            totalAmountTransferred = totalAmountTransferred,
            totalProfit = totalProfit,
            balanceInTransferor = balanceInTransferor,
            balanceInTransferee = balanceInTransferee
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


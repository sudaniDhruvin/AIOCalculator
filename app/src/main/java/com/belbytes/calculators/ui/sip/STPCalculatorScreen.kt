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
    val context = LocalContext.current
    
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
    var stpResult by remember { mutableStateOf<STPResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val scrollState = rememberScrollState()
    
    // Scroll to end when results are shown
    LaunchedEffect(showResults) {
        if (showResults) {
            delay(100) // Small delay to ensure content is rendered
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp) // Space for fixed header
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Investment Amount Input
            STPInputField(
                label = stringResource(R.string.investment_amount),
                placeholder = stringResource(R.string.placeholder_amount_large),
                value = investmentAmount,
                onValueChange = { investmentAmount = it }
            )

            // STP Amount Input
            STPInputField(
                label = stringResource(R.string.stp_amount),
                placeholder = stringResource(R.string.placeholder_amount),
                value = stpAmount,
                onValueChange = { stpAmount = it }
            )

            // Transferor(%) Input
            STPInputField(
                label = stringResource(R.string.transferor),
                placeholder = stringResource(R.string.placeholder_rate_percent),
                value = transferorRate,
                onValueChange = { transferorRate = it }
            )

            // Transferee(%) Input
            STPInputField(
                label = stringResource(R.string.transferee),
                placeholder = stringResource(R.string.placeholder_rate),
                value = transfereeRate,
                onValueChange = { transfereeRate = it }
            )

            // Period Input with Radio Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.period),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        STPPeriodTypeRadioButton(
                            label = stringResource(R.string.years),
                            selected = periodType == STPPeriodType.YEARS,
                            onClick = { periodTypeString = STPPeriodType.YEARS.name }
                        )
                        STPPeriodTypeRadioButton(
                            label = stringResource(R.string.months),
                            selected = periodType == STPPeriodType.MONTHS,
                            onClick = { periodTypeString = STPPeriodType.MONTHS.name }
                        )
                    }
                }
                STPInputField(
                    label = "",
                    placeholder = if (periodType == STPPeriodType.YEARS) "Ex: 6" else "Ex: 72",
                    value = period,
                    onValueChange = { period = it }
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
                        keyboardController?.hide() // Hide keyboard when calculate is clicked
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
                                investmentAmount.isBlank() || (investmentAmount.toDoubleOrNull() ?: -1.0) <= 0 ->
                                    context.getString(R.string.error_valid_investment_amount)
                                stpAmount.isBlank() || (stpAmount.toDoubleOrNull() ?: -1.0) <= 0 ->
                                    context.getString(R.string.error_valid_stp_amount)
                                transferorRate.isBlank() || (transferorRate.toDoubleOrNull() ?: -1.0) <= 0 ->
                                    context.getString(R.string.error_valid_transferor_rate)
                                transfereeRate.isBlank() || transfereeRate.toDoubleOrNull() == null ->
                                    context.getString(R.string.error_valid_transferee_rate)
                                period.isBlank() || (period.toDoubleOrNull() ?: -1.0) <= 0 ->
                                    context.getString(R.string.error_invalid_period)
                                else -> {
                                    val invAmount = investmentAmount.toDoubleOrNull() ?: 0.0
                                    val stpAmt = stpAmount.toDoubleOrNull() ?: 0.0
                                    if (stpAmt >= invAmount) {
                                        context.getString(R.string.error_check_inputs)
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
                                    label = stringResource(R.string.total_amount_transferred),
                                    value = formatCurrencyWithDecimal(context, result.totalAmountTransferred)
                                )
                                STPResultRow(
                                    label = stringResource(R.string.total_profit),
                                    value = formatCurrencyWithDecimal(context, result.totalProfit)
                                )
                                STPResultRow(
                                    label = stringResource(R.string.balance_in_transferor),
                                    value = formatCurrencyWithDecimal(context, result.balanceInTransferor)
                                )
                                STPResultRow(
                                    label = stringResource(R.string.balance_in_transferee),
                                    value = formatCurrencyWithDecimal(context, result.balanceInTransferee)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Fixed Header Overlay - Absolutely positioned, never affected by keyboard
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .align(Alignment.TopStart)
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
                text = stringResource(R.string.stp_calculator),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun STPCalculatorHeader(onBackClick: () -> Unit) {
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
                contentDescription = stringResource(R.string.back),
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = stringResource(R.string.stp_calculator),
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
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
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
fun STPPeriodTypeRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier.padding(end = 0.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF222222),
                unselectedColor = Color(0xFF757575)
            )
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(start = 0.dp)
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



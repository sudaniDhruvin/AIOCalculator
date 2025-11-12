package com.belbytes.calculators.ui.loan

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ln
import kotlin.math.pow

data class MoratoriumResult(
    val noMoratoriumPrincipal: Double,
    val noMoratoriumEMI: Double,
    val noMoratoriumTenure: Int,
    val noMoratoriumTotalInterest: Double,
    val noMoratoriumTotalPayment: Double,
    val moratoriumPrincipal: Double,
    val moratoriumEMI: Double,
    val moratoriumTenure: Int,
    val moratoriumTotalInterest: Double,
    val moratoriumTotalPayment: Double
)

enum class MoratoriumPeriodType {
    YEARS,
    MONTHS
}

@Composable
fun MoratoriumCalculatorScreen(
    onBackClick: () -> Unit
) {
    // State variables
    var loanAmount by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("") }
    var periodTypeString by rememberSaveable { mutableStateOf(MoratoriumPeriodType.YEARS.name) }
    val periodType = remember(periodTypeString) { 
        try {
            MoratoriumPeriodType.valueOf(periodTypeString)
        } catch (e: IllegalArgumentException) {
            MoratoriumPeriodType.YEARS // Default to YEARS if invalid
        }
    }
    var moratoriumPeriod by rememberSaveable { mutableStateOf("") }
    var moratoriumOption by rememberSaveable { mutableStateOf("No change in monthly EMI") }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var moratoriumResult by rememberSaveable { mutableStateOf<MoratoriumResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        MoratoriumCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Loan Amount Input
            MoratoriumInputField(
                label = "Loan Amount",
                placeholder = "Ex: 500,000",
                value = loanAmount,
                onValueChange = { loanAmount = it }
            )

            // Interest % Input
            MoratoriumInputField(
                label = "Interest %",
                placeholder = "Ex: 12%",
                value = interestRate,
                onValueChange = { interestRate = it }
            )

            // Period Input
            MoratoriumInputField(
                label = "Period",
                placeholder = "Ex: 6",
                value = period,
                onValueChange = { period = it }
            )

            // Period Type Radio Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MoratoriumPeriodTypeRadioButton(
                    label = "Years",
                    selected = periodType == MoratoriumPeriodType.YEARS,
                    onClick = { periodTypeString = MoratoriumPeriodType.YEARS.name }
                )
                MoratoriumPeriodTypeRadioButton(
                    label = "Months",
                    selected = periodType == MoratoriumPeriodType.MONTHS,
                    onClick = { periodTypeString = MoratoriumPeriodType.MONTHS.name }
                )
            }

            // Moratorium Period Input
            MoratoriumInputField(
                label = "Moratorium Period",
                placeholder = "Ex: 12",
                value = moratoriumPeriod,
                onValueChange = { moratoriumPeriod = it }
            )

            // Select your option Dropdown
            MoratoriumOptionDropdown(
                label = "Select your option",
                value = moratoriumOption,
                onValueChange = { moratoriumOption = it }
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
                        errorMessage = null
                        val result = calculateMoratorium(
                            loanAmount,
                            interestRate,
                            period,
                            periodType,
                            moratoriumPeriod,
                            moratoriumOption
                        )
                        if (result != null) {
                            moratoriumResult = result
                            showResults = true
                            errorMessage = null
                        } else {
                            showResults = false
                            moratoriumResult = null
                            // Set error message based on validation
                            errorMessage = when {
                                loanAmount.isBlank() || loanAmount.toDoubleOrNull() == null || loanAmount.toDoubleOrNull()!! <= 0 -> 
                                    "Please enter a valid loan amount"
                                interestRate.isBlank() || interestRate.toDoubleOrNull() == null || interestRate.toDoubleOrNull()!! <= 0 -> 
                                    "Please enter a valid interest rate"
                                period.isBlank() || period.toDoubleOrNull() == null || period.toDoubleOrNull()!! <= 0 -> 
                                    "Please enter a valid period"
                                moratoriumPeriod.isBlank() || moratoriumPeriod.toDoubleOrNull() == null || moratoriumPeriod.toDoubleOrNull()!! <= 0 -> 
                                    "Please enter a valid moratorium period"
                                else -> {
                                    val periodValue = period.toDoubleOrNull() ?: 0.0
                                    val moratoriumValue = moratoriumPeriod.toDoubleOrNull() ?: 0.0
                                    val totalMonths = when (periodType) {
                                        MoratoriumPeriodType.YEARS -> {
                                            Math.round(periodValue * 12).toInt()
                                        }
                                        MoratoriumPeriodType.MONTHS -> {
                                            Math.round(periodValue).toInt()
                                        }
                                    }
                                    val moratoriumMonthsInt = Math.round(moratoriumValue).toInt()
                                    if (moratoriumMonthsInt >= totalMonths) {
                                        "Moratorium period must be less than loan tenure (${totalMonths} months)"
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
                        containerColor = Color(0xFF4257B2)
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
                        loanAmount = ""
                        interestRate = ""
                        period = ""
                        periodTypeString = MoratoriumPeriodType.YEARS.name
                        moratoriumPeriod = ""
                        moratoriumOption = "No change in monthly EMI"
                        showResults = false
                        moratoriumResult = null
                        errorMessage = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Reset",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
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
                visible = showResults && moratoriumResult != null,
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
                moratoriumResult?.let { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF7F7F7)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Table Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MoratoriumTableHeaderCell("", modifier = Modifier.weight(1f))
                                MoratoriumTableHeaderCell("No Moratorium", modifier = Modifier.weight(1f))
                                MoratoriumTableHeaderCell("Moratorium Revise EMI", modifier = Modifier.weight(1f))
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Total Principal Row
                            MoratoriumTableRow(
                                label = "Total Principal",
                                noMoratoriumValue = formatCurrencyWithDecimal(result.noMoratoriumPrincipal),
                                moratoriumValue = formatCurrencyWithDecimal(result.moratoriumPrincipal)
                            )
                            
                            // Monthly EMI Row
                            MoratoriumTableRow(
                                label = "Monthly EMI",
                                noMoratoriumValue = formatCurrencyWithDecimal(result.noMoratoriumEMI),
                                moratoriumValue = formatCurrencyWithDecimal(result.moratoriumEMI)
                            )
                            
                            // Tenure Row
                            MoratoriumTableRow(
                                label = "Tenure (in Month)",
                                noMoratoriumValue = "${result.noMoratoriumTenure}.00",
                                moratoriumValue = "${result.moratoriumTenure}.00"
                            )
                            
                            // Total Interest Row
                            MoratoriumTableRow(
                                label = "Total Interest",
                                noMoratoriumValue = formatCurrencyWithDecimal(result.noMoratoriumTotalInterest),
                                moratoriumValue = formatCurrencyWithDecimal(result.moratoriumTotalInterest)
                            )
                            
                            // Total Payment Row
                            MoratoriumTableRow(
                                label = "Total Payment",
                                noMoratoriumValue = formatCurrencyWithDecimal(result.noMoratoriumTotalPayment),
                                moratoriumValue = formatCurrencyWithDecimal(result.moratoriumTotalPayment)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoratoriumCalculatorHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color(0xFF4257B2))
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
            text = "Moratorium Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MoratoriumInputField(
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
fun MoratoriumPeriodTypeRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
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
fun MoratoriumOptionDropdown(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        "No change in monthly EMI",
        "No change in loan tenure"
    )

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    disabledContainerColor = Color(0xFFF5F5F5),
                    disabledBorderColor = Color.Transparent,
                    disabledTextColor = Color.Black
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.Gray
                    )
                }
            )
        }
        
        AnimatedVisibility(
            visible = expanded,
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    options.forEachIndexed { index, option ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(option)
                                    expanded = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        if (index < options.size - 1) {
                            Divider(
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoratoriumTableHeaderCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun MoratoriumTableRow(
    label: String,
    noMoratoriumValue: String,
    moratoriumValue: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Label Column
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        // No Moratorium Column
        Text(
            text = noMoratoriumValue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        
        // Moratorium Column
        Text(
            text = moratoriumValue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

fun calculateMoratorium(
    loanAmount: String,
    interestRate: String,
    period: String,
    periodType: MoratoriumPeriodType,
    moratoriumPeriod: String,
    moratoriumOption: String
): MoratoriumResult? {
    return try {
        val principal = loanAmount.toDoubleOrNull() ?: return null
        val rate = interestRate.toDoubleOrNull() ?: return null
        val periodValue = period.toDoubleOrNull() ?: return null
        val moratoriumMonths = moratoriumPeriod.toDoubleOrNull() ?: return null
        
        // Validate inputs
        if (principal <= 0 || rate <= 0 || periodValue <= 0 || moratoriumMonths <= 0) return null
        
        // Convert period to months based on period type
        val totalMonths = when (periodType) {
            MoratoriumPeriodType.YEARS -> {
                // Convert years to months: multiply by 12
                Math.round(periodValue * 12).toInt()
            }
            MoratoriumPeriodType.MONTHS -> {
                // When period type is MONTHS, use the period value directly (already in months)
                Math.round(periodValue).toInt()
            }
        }
        
        // Ensure totalMonths is at least 1
        if (totalMonths < 1) return null
        
        // Convert moratorium period to integer months (always in months, regardless of period type)
        val moratoriumMonthsInt = Math.round(moratoriumMonths).toInt()
        
        // Validate moratorium period
        // Moratorium period must be at least 1 month and less than total loan tenure
        if (moratoriumMonthsInt < 1) return null
        if (moratoriumMonthsInt >= totalMonths) return null
        
        val monthlyRate = rate / (12 * 100)
        
        // Calculate No Moratorium scenario
        val noMoratoriumEMI = if (monthlyRate > 0) {
            principal * (monthlyRate * (1 + monthlyRate).pow(totalMonths)) / ((1 + monthlyRate).pow(totalMonths) - 1)
        } else {
            principal / totalMonths
        }
        val noMoratoriumTotalPayment = noMoratoriumEMI * totalMonths
        val noMoratoriumTotalInterest = noMoratoriumTotalPayment - principal
        
        // Calculate Moratorium scenario
        // During moratorium, interest accumulates (simple interest)
        val moratoriumInterest = principal * monthlyRate * moratoriumMonthsInt
        val newPrincipal = principal + moratoriumInterest
        
        val moratoriumEMI: Double
        val moratoriumTenure: Int
        
        if (moratoriumOption == "No change in monthly EMI") {
            // Option A: Keep EMI same, extend tenure
            // Formula: n = log(EMI / (EMI - P × r)) / log(1 + r)
            moratoriumEMI = noMoratoriumEMI
            moratoriumTenure = if (monthlyRate > 0 && moratoriumEMI > newPrincipal * monthlyRate) {
                // Calculate new tenure using the formula from documentation
                val tenureMonths = ln(moratoriumEMI / (moratoriumEMI - newPrincipal * monthlyRate)) / ln(1 + monthlyRate)
                Math.round(tenureMonths).toInt()
            } else {
                totalMonths
            }
        } else {
            // Option B: Keep tenure same, increase EMI
            // New tenure = original tenure - moratorium period (as per documentation)
            moratoriumTenure = maxOf(1, totalMonths - moratoriumMonthsInt)
            moratoriumEMI = if (monthlyRate > 0) {
                // Calculate new EMI using standard formula with new principal and reduced tenure
                newPrincipal * (monthlyRate * (1 + monthlyRate).pow(moratoriumTenure)) / ((1 + monthlyRate).pow(moratoriumTenure) - 1)
            } else {
                newPrincipal / moratoriumTenure
            }
        }
        
        val moratoriumTotalPayment = moratoriumEMI * moratoriumTenure
        // Total Interest = Total Payment - New Principal (as per documentation)
        val moratoriumTotalInterest = moratoriumTotalPayment - newPrincipal
        
        MoratoriumResult(
            noMoratoriumPrincipal = principal,
            noMoratoriumEMI = noMoratoriumEMI,
            noMoratoriumTenure = totalMonths,
            noMoratoriumTotalInterest = noMoratoriumTotalInterest,
            noMoratoriumTotalPayment = noMoratoriumTotalPayment,
            moratoriumPrincipal = newPrincipal,
            moratoriumEMI = moratoriumEMI,
            moratoriumTenure = moratoriumTenure,
            moratoriumTotalInterest = moratoriumTotalInterest,
            moratoriumTotalPayment = moratoriumTotalPayment
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


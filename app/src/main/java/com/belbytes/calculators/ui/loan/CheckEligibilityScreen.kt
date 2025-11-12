package com.belbytes.calculators.ui.loan

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
import kotlin.math.pow

data class EligibilityResult(
    val eligibleEMI: Double,
    val eligibleLoanAmount: Double
)

enum class EligibilityPeriodType {
    YEARS,
    MONTHS
}

@Composable
fun CheckEligibilityScreen(
    onBackClick: () -> Unit
) {
    // State variables
    var grossMonthlyIncome by rememberSaveable { mutableStateOf("") }
    var foirPercent by rememberSaveable { mutableStateOf("40") }
    var totalMonthlyEMIs by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("") }
    var periodTypeString by rememberSaveable { mutableStateOf(EligibilityPeriodType.YEARS.name) }
    val periodType = remember(periodTypeString) { 
        EligibilityPeriodType.valueOf(periodTypeString)
    }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var eligibilityResult by rememberSaveable { mutableStateOf<EligibilityResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        CheckEligibilityHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Gross Monthly Income Input
            EligibilityInputField(
                label = "Gross Monthly Income",
                placeholder = "Ex: 10000",
                value = grossMonthlyIncome,
                onValueChange = { grossMonthlyIncome = it }
            )

            // FOIR Dropdown
            EligibilityFOIRDropdown(
                label = "FOIR(MAX. %of salary)",
                value = foirPercent,
                onValueChange = { foirPercent = it }
            )

            // Total Monthly EMI's Input
            EligibilityInputField(
                label = "Total Monthly EMI's",
                placeholder = "Ex: 5000",
                value = totalMonthlyEMIs,
                onValueChange = { totalMonthlyEMIs = it }
            )

            // Interest % Input
            EligibilityInputField(
                label = "Interest %",
                placeholder = "Ex: 12",
                value = interestRate,
                onValueChange = { interestRate = it }
            )

            // Period Input
            EligibilityInputField(
                label = "Period (Years)",
                placeholder = "Ex: 6",
                value = period,
                onValueChange = { period = it }
            )

            // Period Type Radio Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                EligibilityPeriodTypeRadioButton(
                    label = "Years",
                    selected = periodType == EligibilityPeriodType.YEARS,
                    onClick = { periodTypeString = EligibilityPeriodType.YEARS.name }
                )
                EligibilityPeriodTypeRadioButton(
                    label = "Months",
                    selected = periodType == EligibilityPeriodType.MONTHS,
                    onClick = { periodTypeString = EligibilityPeriodType.MONTHS.name }
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
                        when {
                            grossMonthlyIncome.isBlank() || grossMonthlyIncome.toDoubleOrNull() == null || grossMonthlyIncome.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid gross monthly income"
                                showResults = false
                                eligibilityResult = null
                            }
                            foirPercent.isBlank() || foirPercent.toDoubleOrNull() == null || foirPercent.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid FOIR percentage"
                                showResults = false
                                eligibilityResult = null
                            }
                            totalMonthlyEMIs.isBlank() || totalMonthlyEMIs.toDoubleOrNull() == null || totalMonthlyEMIs.toDoubleOrNull()!! < 0 -> {
                                errorMessage = "Please enter a valid total monthly EMIs"
                                showResults = false
                                eligibilityResult = null
                            }
                            interestRate.isBlank() || interestRate.toDoubleOrNull() == null || interestRate.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid interest rate"
                                showResults = false
                                eligibilityResult = null
                            }
                            period.isBlank() || period.toDoubleOrNull() == null || period.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid period"
                                showResults = false
                                eligibilityResult = null
                            }
                            else -> {
                                val result = calculateEligibility(
                                    grossMonthlyIncome,
                                    foirPercent,
                                    totalMonthlyEMIs,
                                    interestRate,
                                    period,
                                    periodType
                                )
                                if (result != null) {
                                    eligibilityResult = result
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Please check all input values"
                                    showResults = false
                                    eligibilityResult = null
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
                        grossMonthlyIncome = ""
                        foirPercent = "40"
                        totalMonthlyEMIs = ""
                        interestRate = ""
                        period = ""
                        periodTypeString = EligibilityPeriodType.YEARS.name
                        showResults = false
                        eligibilityResult = null
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

            // Error Message Display
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
                        color = Color(0xFFC62828),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Results Section
            AnimatedVisibility(
                visible = showResults && eligibilityResult != null,
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
                eligibilityResult?.let { result ->
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
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Eligible EMI
                            EligibilityResultRow(
                                "Eligible EMI",
                                formatCurrencyWithDecimal(result.eligibleEMI)
                            )
                            
                            // Eligible Loan Amount
                            EligibilityResultRow(
                                "Eligible Loan Amount",
                                formatCurrencyWithDecimal(result.eligibleLoanAmount)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckEligibilityHeader(onBackClick: () -> Unit) {
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
            text = "Check Eligibility",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EligibilityInputField(
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
fun EligibilityFOIRDropdown(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val foirOptions = listOf("35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95")

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
                    foirOptions.forEachIndexed { index, option ->
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
                        if (index < foirOptions.size - 1) {
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
fun EligibilityPeriodTypeRadioButton(
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
fun EligibilityResultRow(
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
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

fun calculateEligibility(
    grossMonthlyIncome: String,
    foirPercent: String,
    totalMonthlyEMIs: String,
    interestRate: String,
    period: String,
    periodType: EligibilityPeriodType
): EligibilityResult? {
    return try {
        val income = grossMonthlyIncome.toDoubleOrNull() ?: return null
        val foir = foirPercent.toDoubleOrNull() ?: return null
        val existingEMIs = totalMonthlyEMIs.toDoubleOrNull() ?: 0.0
        val rate = interestRate.toDoubleOrNull() ?: return null
        val periodValue = period.toDoubleOrNull() ?: return null
        
        if (income <= 0 || rate <= 0 || periodValue <= 0) return null
        
        // Convert period to months
        val totalMonths = if (periodType == EligibilityPeriodType.YEARS) {
            (periodValue * 12).toInt()
        } else {
            periodValue.toInt()
        }
        
        // Calculate eligible EMI
        // FOIR = (Total EMI / Gross Monthly Income) * 100
        // Eligible EMI = (FOIR / 100) * Gross Monthly Income - Existing EMIs
        val maxEMI = (foir / 100.0) * income
        val eligibleEMI = maxEMI - existingEMIs
        
        if (eligibleEMI <= 0) {
            return EligibilityResult(
                eligibleEMI = 0.0,
                eligibleLoanAmount = 0.0
            )
        }
        
        // Calculate eligible loan amount using EMI formula
        // EMI = P * r * (1 + r)^n / ((1 + r)^n - 1)
        // P = EMI * ((1 + r)^n - 1) / (r * (1 + r)^n)
        val monthlyRate = rate / (12 * 100)
        
        val eligibleLoanAmount = if (monthlyRate > 0) {
            eligibleEMI * ((1 + monthlyRate).pow(totalMonths) - 1) / (monthlyRate * (1 + monthlyRate).pow(totalMonths))
        } else {
            eligibleEMI * totalMonths
        }
        
        EligibilityResult(
            eligibleEMI = eligibleEMI,
            eligibleLoanAmount = eligibleLoanAmount
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


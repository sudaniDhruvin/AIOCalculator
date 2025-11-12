package com.belbytes.calculators.ui.emi

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoanComparisonResult(
    val loan1MonthlyEMI: Double,
    val loan1TotalInterest: Double,
    val loan1TotalPayment: Double,
    val loan2MonthlyEMI: Double,
    val loan2TotalInterest: Double,
    val loan2TotalPayment: Double
) : Parcelable

@Composable
fun CompareLoansScreen(
    onBackClick: () -> Unit,
    onCompareMoreClick: (List<LoanTableEntry>) -> Unit = { }
) {
    // Loan 1 State
    var loan1Amount by rememberSaveable { mutableStateOf("") }
    var loan1InterestRate by rememberSaveable { mutableStateOf("") }
    var loan1Period by rememberSaveable { mutableStateOf("") }
    var loan1PeriodType by rememberSaveable { mutableStateOf("Years") } // "Years" or "Month"

    // Loan 2 State
    var loan2Amount by rememberSaveable { mutableStateOf("") }
    var loan2InterestRate by rememberSaveable { mutableStateOf("") }
    var loan2Period by rememberSaveable { mutableStateOf("") }
    var loan2PeriodType by rememberSaveable { mutableStateOf("Years") } // "Years" or "Month"

    // Results State
    var showResults by rememberSaveable { mutableStateOf(false) }
    var comparisonResult by rememberSaveable { mutableStateOf<LoanComparisonResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    
    val scrollState = rememberScrollState()
    
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
        CompareLoansHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Loan 1 Section
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Loan 1",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Amount Input
                CompareLoansInputField(
                    label = "Amount",
                    placeholder = "Ex: 500,000",
                    value = loan1Amount,
                    onValueChange = { loan1Amount = it }
                )

                // Interest Rate Input
                CompareLoansInputField(
                    label = "Interest Rate (%)",
                    placeholder = "Ex: 5%",
                    value = loan1InterestRate,
                    onValueChange = { loan1InterestRate = it }
                )

                // Period Input
                CompareLoansInputField(
                    label = "Period",
                    placeholder = "Ex: 10",
                    value = loan1Period,
                    onValueChange = { loan1Period = it }
                )

                // Period Type Radio Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompareLoansRadioButton(
                        label = "Years",
                        selected = loan1PeriodType == "Years",
                        onClick = { loan1PeriodType = "Years" }
                    )
                    CompareLoansRadioButton(
                        label = "Month",
                        selected = loan1PeriodType == "Month",
                        onClick = { loan1PeriodType = "Month" }
                    )
                }
            }

            // Loan 2 Section
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Loan 2",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Amount Input
                CompareLoansInputField(
                    label = "Amount",
                    placeholder = "Ex: 500,000",
                    value = loan2Amount,
                    onValueChange = { loan2Amount = it }
                )

                // Interest Rate Input
                CompareLoansInputField(
                    label = "Interest Rate (%)",
                    placeholder = "Ex: 5%",
                    value = loan2InterestRate,
                    onValueChange = { loan2InterestRate = it }
                )

                // Period Input
                CompareLoansInputField(
                    label = "Period",
                    placeholder = "Ex: 10",
                    value = loan2Period,
                    onValueChange = { loan2Period = it }
                )

                // Period Type Radio Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompareLoansRadioButton(
                        label = "Years",
                        selected = loan2PeriodType == "Years",
                        onClick = { loan2PeriodType = "Years" }
                    )
                    CompareLoansRadioButton(
                        label = "Month",
                        selected = loan2PeriodType == "Month",
                        onClick = { loan2PeriodType = "Month" }
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Calculate Button
                Button(
                    onClick = {
                        errorMessage = null
                        when {
                            loan1Amount.isBlank() || loan1Amount.toDoubleOrNull() == null || loan1Amount.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid Loan 1 amount"
                                showResults = false
                                comparisonResult = null
                            }
                            loan1InterestRate.isBlank() || loan1InterestRate.toDoubleOrNull() == null || loan1InterestRate.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid Loan 1 interest rate"
                                showResults = false
                                comparisonResult = null
                            }
                            loan1Period.isBlank() || loan1Period.toDoubleOrNull() == null || loan1Period.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid Loan 1 period"
                                showResults = false
                                comparisonResult = null
                            }
                            loan2Amount.isBlank() || loan2Amount.toDoubleOrNull() == null || loan2Amount.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid Loan 2 amount"
                                showResults = false
                                comparisonResult = null
                            }
                            loan2InterestRate.isBlank() || loan2InterestRate.toDoubleOrNull() == null || loan2InterestRate.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid Loan 2 interest rate"
                                showResults = false
                                comparisonResult = null
                            }
                            loan2Period.isBlank() || loan2Period.toDoubleOrNull() == null || loan2Period.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid Loan 2 period"
                                showResults = false
                                comparisonResult = null
                            }
                            else -> {
                                val result = calculateLoanComparison(
                                    loan1Amount = loan1Amount,
                                    loan1InterestRate = loan1InterestRate,
                                    loan1Period = loan1Period,
                                    loan1PeriodType = loan1PeriodType,
                                    loan2Amount = loan2Amount,
                                    loan2InterestRate = loan2InterestRate,
                                    loan2Period = loan2Period,
                                    loan2PeriodType = loan2PeriodType
                                )
                                if (result != null) {
                                    comparisonResult = result
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Please check all input values"
                                    showResults = false
                                    comparisonResult = null
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
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
                        loan1Amount = ""
                        loan1InterestRate = ""
                        loan1Period = ""
                        loan1PeriodType = "Years"
                        loan2Amount = ""
                        loan2InterestRate = ""
                        loan2Period = ""
                        loan2PeriodType = "Years"
                        showResults = false
                        comparisonResult = null
                        errorMessage = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
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
                visible = showResults && comparisonResult != null,
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
                comparisonResult?.let { result ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Monthly EMI Card
                        CompareResultCard(
                            title = "Monthly EMI",
                            loan1Value = result.loan1MonthlyEMI,
                            loan2Value = result.loan2MonthlyEMI
                        )

                        // Total Interest Card
                        CompareResultCard(
                            title = "Total Interest",
                            loan1Value = result.loan1TotalInterest,
                            loan2Value = result.loan2TotalInterest
                        )

                        // Total Payment Card
                        CompareResultCard(
                            title = "Total Payment",
                            loan1Value = result.loan1TotalPayment,
                            loan2Value = result.loan2TotalPayment
                        )

                    }
                }
            }
            
            // Compare More Button - Outside AnimatedVisibility but visible when results are shown
            AnimatedVisibility(
                visible = showResults && comparisonResult != null,
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
                Button(
                    onClick = {
                        comparisonResult?.let { result ->
                            // Get loan data from state
                            val amount1 = loan1Amount.toDoubleOrNull() ?: 0.0
                            val rate1 = loan1InterestRate.toDoubleOrNull() ?: 0.0
                            val period1Value = loan1Period.toDoubleOrNull() ?: 0.0
                            val months1 = if (loan1PeriodType == "Years") {
                                (period1Value * 12).toInt()
                            } else {
                                period1Value.toInt()
                            }

                            val amount2 = loan2Amount.toDoubleOrNull() ?: 0.0
                            val rate2 = loan2InterestRate.toDoubleOrNull() ?: 0.0
                            val period2Value = loan2Period.toDoubleOrNull() ?: 0.0
                            val months2 = if (loan2PeriodType == "Years") {
                                (period2Value * 12).toInt()
                            } else {
                                period2Value.toInt()
                            }

                            val loans = listOf(
                                LoanTableEntry(
                                    loanAmount = amount1,
                                    interestRate = rate1,
                                    periodMonths = months1,
                                    monthlyEMI = result.loan1MonthlyEMI,
                                    totalInterest = result.loan1TotalInterest,
                                    totalPayment = result.loan1TotalPayment
                                ),
                                LoanTableEntry(
                                    loanAmount = amount2,
                                    interestRate = rate2,
                                    periodMonths = months2,
                                    monthlyEMI = result.loan2MonthlyEMI,
                                    totalInterest = result.loan2TotalInterest,
                                    totalPayment = result.loan2TotalPayment
                                )
                            )
                            onCompareMoreClick(loans)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(top = 16.dp).padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                ) {
                    Text(
                        text = "Compare More",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Compare More",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CompareLoansHeader(onBackClick: () -> Unit) {
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
            text = "Compare Loans",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CompareLoansInputField(
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
fun CompareLoansRadioButton(
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
fun CompareResultCard(
    title: String,
    loan1Value: Double,
    loan2Value: Double
) {
    val difference = loan2Value - loan1Value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title - Centered
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            // Comparative Values - Centered horizontally
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Loan 1 Value (Green)
                Text(
                    text = formatCurrencyWithDecimal(loan1Value),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(end = 20.dp)
                )

                // Loan 2 Value (Red)
                Text(
                    text = formatCurrencyWithDecimal(loan2Value),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF3235E)
                )
            }

            // Difference - Centered
            Text(
                text = "Difference : ${formatCurrencyWithDecimal(difference)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF2196F3),
                textAlign = TextAlign.Center
            )
        }
    }
}

fun calculateLoanComparison(
    loan1Amount: String,
    loan1InterestRate: String,
    loan1Period: String,
    loan1PeriodType: String,
    loan2Amount: String,
    loan2InterestRate: String,
    loan2Period: String,
    loan2PeriodType: String
): LoanComparisonResult? {
    return try {
        val amount1 = loan1Amount.toDoubleOrNull() ?: return null
        val rate1 = loan1InterestRate.toDoubleOrNull() ?: return null
        val period1Value = loan1Period.toDoubleOrNull() ?: return null
        val months1 = if (loan1PeriodType == "Years") {
            (period1Value * 12).toInt()
        } else {
            period1Value.toInt()
        }

        val amount2 = loan2Amount.toDoubleOrNull() ?: return null
        val rate2 = loan2InterestRate.toDoubleOrNull() ?: return null
        val period2Value = loan2Period.toDoubleOrNull() ?: return null
        val months2 = if (loan2PeriodType == "Years") {
            (period2Value * 12).toInt()
        } else {
            period2Value.toInt()
        }

        // Calculate Loan 1 EMI
        val monthlyRate1 = rate1 / (12 * 100)
        val monthlyEMI1 = if (monthlyRate1 > 0) {
            val rateFactor1 = java.lang.Math.pow(1 + monthlyRate1, months1.toDouble())
            amount1 * monthlyRate1 * rateFactor1 / (rateFactor1 - 1)
        } else {
            amount1 / months1
        }
        val totalPayment1 = monthlyEMI1 * months1
        val totalInterest1 = totalPayment1 - amount1

        // Calculate Loan 2 EMI
        val monthlyRate2 = rate2 / (12 * 100)
        val monthlyEMI2 = if (monthlyRate2 > 0) {
            val rateFactor2 = java.lang.Math.pow(1 + monthlyRate2, months2.toDouble())
            amount2 * monthlyRate2 * rateFactor2 / (rateFactor2 - 1)
        } else {
            amount2 / months2
        }
        val totalPayment2 = monthlyEMI2 * months2
        val totalInterest2 = totalPayment2 - amount2

        LoanComparisonResult(
            loan1MonthlyEMI = monthlyEMI1,
            loan1TotalInterest = totalInterest1,
            loan1TotalPayment = totalPayment1,
            loan2MonthlyEMI = monthlyEMI2,
            loan2TotalInterest = totalInterest2,
            loan2TotalPayment = totalPayment2
        )
    } catch (e: Exception) {
        null
    }
}

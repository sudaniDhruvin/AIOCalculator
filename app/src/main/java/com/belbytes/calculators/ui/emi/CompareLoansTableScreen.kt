package com.belbytes.calculators.ui.emi

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoanTableEntry(
    val loanAmount: Double,
    val interestRate: Double,
    val periodMonths: Int,
    val monthlyEMI: Double,
    val totalInterest: Double,
    val totalPayment: Double
) : Parcelable

@Composable
fun CompareLoansTableScreen(
    onBackClick: () -> Unit,
    initialLoans: List<LoanTableEntry> = emptyList()
) {
    var loans by remember { mutableStateOf(initialLoans) }
    var showAddLoanDialog by remember { mutableStateOf(false) }
    
    // Add Loan Dialog State
    var amount by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("") }
    var periodType by rememberSaveable { mutableStateOf("Years") } // "Years" or "Month"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        CompareLoansTableHeader(onBackClick = onBackClick)

        // Table Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 35.dp)
        ) {
            // Scrollable Table Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Comparison Table Card with horizontal scrolling
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    val displayLoans = if (loans.isEmpty()) {
                        // Show default loans matching Figma design (4 identical entries)
                        val defaultLoan = LoanTableEntry(
                            loanAmount = 100000.0,
                            interestRate = 12.0,
                            periodMonths = 72,
                            monthlyEMI = 7352.80,
                            totalInterest = 2083.33,
                            totalPayment = 49264.77
                        )
                        List(4) { defaultLoan }
                    } else {
                        loans
                    }
                    
                    // Table with labels in rows and data in horizontally scrollable columns
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Fixed label column with proper width for two-line labels
                        Column(
                            modifier = Modifier.width(160.dp)
                        ) {
                            // Header cell for label column
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(
                                        Color(0xFF2196F3),
                                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 0.dp)
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            
                            // Label rows with consistent height
                            val labels = listOf(
                                "Loan Amount",
                                "%",
                                "Period (M)",
                                "Monthly EMI",
                                "Total Interest",
                                "Total Payment"
                            )
                            labels.forEachIndexed { index, label ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .background(
                                            if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
                                        )
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        maxLines = 2,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                        
                        // Horizontally scrollable data columns
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            itemsIndexed(displayLoans) { index, entry ->
                                LoanDataColumn(
                                    entry = entry, 
                                    index = index,
                                    isFirst = index == 0,
                                    isLast = index == displayLoans.size - 1
                                )
                            }
                        }
                    }
                }
            }
            
            // Add Loan Button - Fixed at bottom
            Button(
                onClick = {
                    showAddLoanDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Loan",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Loan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        // Add Loan Dialog
        if (showAddLoanDialog) {
            AddLoanDialog(
                amount = amount,
                interestRate = interestRate,
                period = period,
                periodType = periodType,
                onAmountChange = { amount = it },
                onInterestRateChange = { interestRate = it },
                onPeriodChange = { period = it },
                onPeriodTypeChange = { periodType = it },
                onAddToCompare = {
                    val newLoan = calculateLoanEntry(amount, interestRate, period, periodType)
                    if (newLoan != null) {
                        loans = loans + newLoan
                        // Reset form
                        amount = ""
                        interestRate = ""
                        period = ""
                        periodType = "Years"
                        showAddLoanDialog = false
                    } else {
                        // This shouldn't happen if validation is correct, but handle it anyway
                        showAddLoanDialog = false
                    }
                },
                onCancel = {
                    showAddLoanDialog = false
                }
            )
        }
    }
}

fun calculateLoanEntry(
    amount: String,
    interestRate: String,
    period: String,
    periodType: String
): LoanTableEntry? {
    return try {
        val principal = amount.toDoubleOrNull() ?: return null
        val rate = interestRate.toDoubleOrNull() ?: return null
        val periodValue = period.toDoubleOrNull() ?: return null
        
        val months = if (periodType == "Years") {
            (periodValue * 12).toInt()
        } else {
            periodValue.toInt()
        }
        
        val monthlyRate = rate / (12 * 100)
        val monthlyEMI = if (monthlyRate > 0) {
            val rateFactor = java.lang.Math.pow(1 + monthlyRate, months.toDouble())
            principal * monthlyRate * rateFactor / (rateFactor - 1)
        } else {
            principal / months
        }
        
        val totalPayment = monthlyEMI * months
        val totalInterest = totalPayment - principal
        
        LoanTableEntry(
            loanAmount = principal,
            interestRate = rate,
            periodMonths = months,
            monthlyEMI = monthlyEMI,
            totalInterest = totalInterest,
            totalPayment = totalPayment
        )
    } catch (e: Exception) {
        null
    }
}

@Composable
fun AddLoanDialog(
    amount: String,
    interestRate: String,
    period: String,
    periodType: String,
    onAmountChange: (String) -> Unit,
    onInterestRateChange: (String) -> Unit,
    onPeriodChange: (String) -> Unit,
    onPeriodTypeChange: (String) -> Unit,
    onAddToCompare: () -> Unit,
    onCancel: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title
                Text(
                    text = "Add New Loan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                // Amount Input
                AddLoanInputField(
                    label = "Amount",
                    placeholder = "Ex: 500,000",
                    value = amount,
                    onValueChange = onAmountChange
                )
                
                // Interest Rate Input
                AddLoanInputField(
                    label = "Interest Rate (%)",
                    placeholder = "Ex: 5%",
                    value = interestRate,
                    onValueChange = onInterestRateChange
                )
                
                // Period Input
                AddLoanInputField(
                    label = "Period",
                    placeholder = "Ex: 10",
                    value = period,
                    onValueChange = onPeriodChange
                )
                
                // Period Type Radio Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AddLoanRadioButton(
                        label = "Years",
                        selected = periodType == "Years",
                        onClick = { onPeriodTypeChange("Years") }
                    )
                    AddLoanRadioButton(
                        label = "Month",
                        selected = periodType == "Month",
                        onClick = { onPeriodTypeChange("Month") }
                    )
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
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add to Compare Button
                    Button(
                        onClick = {
                            errorMessage = null
                            when {
                                amount.isBlank() || (amount.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                    errorMessage = "Please enter a valid loan amount"
                                }
                                interestRate.isBlank() || (interestRate.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                    errorMessage = "Please enter a valid interest rate"
                                }
                                period.isBlank() || (period.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                    errorMessage = "Please enter a valid period"
                                }
                                else -> {
                                    errorMessage = null
                                    onAddToCompare()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Add to",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = "Compare",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Button(
                        onClick = onCancel,
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
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddLoanInputField(
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
fun AddLoanRadioButton(
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
fun CompareLoansTableHeader(onBackClick: () -> Unit) {
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
fun RowScope.TableHeaderCell(
    text: String,
    weight: Float,
    alignment: TextAlign
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.weight(weight),
        textAlign = alignment
    )
}

@Composable
fun LoanDataColumn(
    entry: LoanTableEntry,
    index: Int,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier.width(120.dp)
    ) {
        // Header cell - no rounded corners, seamless connection, fixed height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF2196F3))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loan ${index + 1}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        
        // Data cells - all center-aligned with consistent height matching labels
        val values = listOf(
            formatCurrencyWithDecimal(entry.loanAmount),
            entry.interestRate.toInt().toString(),
            entry.periodMonths.toString(),
            formatCurrencyWithDecimal(entry.monthlyEMI),
            formatCurrencyWithDecimal(entry.totalInterest),
            formatCurrencyWithDecimal(entry.totalPayment)
        )
        
        values.forEachIndexed { cellIndex, value ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        if (cellIndex % 2 == 0) Color.White else Color(0xFFF5F5F5)
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun LoanTableRow(
    entry: LoanTableEntry,
    index: Int,
    isLast: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
                )
                .padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Loan Amount - Single line, right-aligned
            TableDataCell(
                text = formatCurrencyWithDecimal(entry.loanAmount),
                weight = 1.2f,
                alignment = TextAlign.End
            )
            
            // Interest Rate - Single line, center-aligned
            TableDataCell(
                text = entry.interestRate.toInt().toString(),
                weight = 0.8f,
                alignment = TextAlign.Center
            )
            
            // Period - Single line, center-aligned
            TableDataCell(
                text = entry.periodMonths.toString(),
                weight = 1f,
                alignment = TextAlign.Center
            )
            
            // Monthly EMI - Single line, right-aligned
            TableDataCell(
                text = formatCurrencyWithDecimal(entry.monthlyEMI),
                weight = 1.2f,
                alignment = TextAlign.End
            )
            
            // Total Interest - Single line, right-aligned
            TableDataCell(
                text = formatCurrencyWithDecimal(entry.totalInterest),
                weight = 1.2f,
                alignment = TextAlign.End
            )
            
            // Total Payment - Single line, right-aligned
            TableDataCell(
                text = formatCurrencyWithDecimal(entry.totalPayment),
                weight = 1.2f,
                alignment = TextAlign.End
            )
        }
    }
}

@Composable
fun RowScope.TableDataCell(
    text: String,
    weight: Float,
    alignment: TextAlign
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = Color.Black,
        modifier = Modifier.weight(weight),
        textAlign = alignment
    )
}

@Composable
fun RowScope.TableDataCellTwoLines(
    value: Double,
    weight: Float,
    alignment: TextAlign
) {
    val formattedValue = formatCurrencyWithDecimal(value)
    // Split by decimal point, handling comma-separated thousands
    val parts = formattedValue.split(".")
    val integerPart = parts[0] // This includes commas for thousands
    val decimalPart = if (parts.size > 1) parts[1] else ""
    
    Column(
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight(),
        horizontalAlignment = when (alignment) {
            TextAlign.Start -> Alignment.Start
            TextAlign.End -> Alignment.End
            else -> Alignment.CenterHorizontally
        },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = integerPart,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            textAlign = alignment
        )
        if (decimalPart.isNotEmpty()) {
            Text(
                text = ".$decimalPart",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = alignment
            )
        }
    }
}


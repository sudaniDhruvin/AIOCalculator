package com.belbytes.calculators.ui.emi

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Comparison Table Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Table Header Row with rounded top corners
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFF2079EC),
                                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                )
                                .padding(vertical = 14.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableHeaderCell("Loan Amount", weight = 1.2f, alignment = TextAlign.Start)
                            TableHeaderCell("%", weight = 0.8f, alignment = TextAlign.Center)
                            TableHeaderCell("Period (M)", weight = 1f, alignment = TextAlign.Center)
                            TableHeaderCell("Monthly EMI", weight = 1.2f, alignment = TextAlign.End)
                            TableHeaderCell("Total Interest", weight = 1.2f, alignment = TextAlign.End)
                            TableHeaderCell("Total Payment", weight = 1.2f, alignment = TextAlign.End)
                        }

                        // Table Data Rows
                        if (loans.isEmpty()) {
                            // Show default loans matching Figma design (4 identical rows)
                            val defaultLoan = LoanTableEntry(
                                loanAmount = 100000.0,
                                interestRate = 12.0,
                                periodMonths = 72,
                                monthlyEMI = 7352.80,
                                totalInterest = 2083.33,
                                totalPayment = 49264.77
                            )
                            repeat(4) { index ->
                                LoanTableRow(
                                    entry = defaultLoan,
                                    index = index,
                                    isLast = index == 3
                                )
                            }
                        } else {
                            loans.forEachIndexed { index, entry ->
                                LoanTableRow(
                                    entry = entry,
                                    index = index,
                                    isLast = index == loans.size - 1
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
                    containerColor = Color(0xFF2079EC)
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
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add to Compare Button
                    Button(
                        onClick = onAddToCompare,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2079EC)
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
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                            Text(
                                text = "Compare",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 16.sp
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
                focusedBorderColor = Color.Transparent
            ),
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
fun CompareLoansTableHeader(onBackClick: () -> Unit) {
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


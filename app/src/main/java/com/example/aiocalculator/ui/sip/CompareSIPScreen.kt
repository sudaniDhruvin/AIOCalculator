package com.example.aiocalculator.ui.sip

import android.os.Parcelable
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.parcelize.Parcelize
import kotlin.math.pow

@Parcelize
data class SIPTableEntry(
    val monthlyInvestment: Double,
    val expectedReturnRate: Double,
    val periodMonths: Double,
    val investedAmount: Double,
    val estimatedReturn: Double,
    val totalValue: Double
) : Parcelable

@Composable
fun CompareSIPScreen(
    onBackClick: () -> Unit,
    initialSIPs: List<SIPTableEntry> = emptyList()
) {
    var sips by remember { mutableStateOf(initialSIPs) }
    var showAddSIPDialog by remember { mutableStateOf(false) }
    
    // Add SIP Dialog State
    var monthlyInvestment by rememberSaveable { mutableStateOf("") }
    var expectedReturnRate by rememberSaveable { mutableStateOf("") }
    var timePeriod by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        CompareSIPHeader(onBackClick = onBackClick)

        // Table Content
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Table Header Row with blue background
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2079EC))
                                .padding(vertical = 14.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SIPTableHeaderCell("Monthly investment", weight = 1.2f, alignment = TextAlign.Start)
                            SIPTableHeaderCell("%", weight = 0.8f, alignment = TextAlign.Center)
                            SIPTableHeaderCell("Period (M)", weight = 1f, alignment = TextAlign.Center)
                            SIPTableHeaderCell("Invested Amount", weight = 1.2f, alignment = TextAlign.End)
                            SIPTableHeaderCell("Est. Return", weight = 1.2f, alignment = TextAlign.End)
                            SIPTableHeaderCell("Total Value", weight = 1.2f, alignment = TextAlign.End)
                        }

                        // Table Data Rows
                        if (sips.isEmpty()) {
                            // Show empty state - just header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No SIP entries yet. Add a SIP to compare.",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            sips.forEachIndexed { index, entry ->
                                SIPTableRow(
                                    entry = entry,
                                    index = index,
                                    isLast = index == sips.size - 1
                                )
                            }
                        }
                    }
                }
            }
            
            // Add Loan Button - Fixed at bottom right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = {
                        showAddSIPDialog = true
                    },
                    modifier = Modifier
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2079EC)
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add SIP",
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
        }
        
        // Add SIP Dialog
        if (showAddSIPDialog) {
            AddSIPDialog(
                monthlyInvestment = monthlyInvestment,
                expectedReturnRate = expectedReturnRate,
                timePeriod = timePeriod,
                onMonthlyInvestmentChange = { monthlyInvestment = it },
                onExpectedReturnRateChange = { expectedReturnRate = it },
                onTimePeriodChange = { timePeriod = it },
                onAddToCompare = {
                    val newSIP = calculateSIPEntry(monthlyInvestment, expectedReturnRate, timePeriod)
                    if (newSIP != null) {
                        sips = sips + newSIP
                        // Reset form
                        monthlyInvestment = ""
                        expectedReturnRate = ""
                        timePeriod = ""
                        showAddSIPDialog = false
                    }
                },
                onCancel = {
                    showAddSIPDialog = false
                }
            )
        }
    }
}

fun calculateSIPEntry(
    monthlyInvestment: String,
    expectedReturnRate: String,
    timePeriod: String
): SIPTableEntry? {
    return try {
        val monthly = monthlyInvestment.toDoubleOrNull() ?: return null
        val rate = expectedReturnRate.toDoubleOrNull() ?: return null
        val periodYears = timePeriod.toDoubleOrNull() ?: return null
        
        if (monthly <= 0 || rate < 0 || periodYears <= 0) return null
        
        // Convert period from years to months
        val periodMonths = periodYears * 12
        
        // SIP Formula: Maturity Amount = P * [((1 + r)^n - 1) / r] * (1 + r)
        // Where P = Monthly Investment, r = Monthly Interest Rate, n = Number of Months
        val monthlyRate = rate / (12 * 100) // Convert annual rate to monthly rate
        
        val maturityAmount = if (monthlyRate > 0) {
            monthly * (((1 + monthlyRate).pow(periodMonths.toInt()) - 1) / monthlyRate) * (1 + monthlyRate)
        } else {
            monthly * periodMonths
        }
        
        val investedAmount = monthly * periodMonths
        val estimatedReturn = maturityAmount - investedAmount
        
        SIPTableEntry(
            monthlyInvestment = monthly,
            expectedReturnRate = rate,
            periodMonths = periodMonths,
            investedAmount = investedAmount,
            estimatedReturn = estimatedReturn,
            totalValue = maturityAmount
        )
    } catch (e: Exception) {
        null
    }
}

@Composable
fun AddSIPDialog(
    monthlyInvestment: String,
    expectedReturnRate: String,
    timePeriod: String,
    onMonthlyInvestmentChange: (String) -> Unit,
    onExpectedReturnRateChange: (String) -> Unit,
    onTimePeriodChange: (String) -> Unit,
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
                    text = "Add New SIP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                // Monthly Investment Input
                AddSIPInputField(
                    label = "Monthly Investment",
                    placeholder = "Ex: 1,00,000",
                    value = monthlyInvestment,
                    onValueChange = onMonthlyInvestmentChange
                )
                
                // Expected Returns Rate Input
                AddSIPInputField(
                    label = "Expected Returns Rate %",
                    placeholder = "10% (p.a)",
                    value = expectedReturnRate,
                    onValueChange = onExpectedReturnRateChange
                )
                
                // Time Period Input
                AddSIPInputField(
                    label = "Time Period",
                    placeholder = "Ex: 1",
                    value = timePeriod,
                    onValueChange = onTimePeriodChange
                )
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEBEBEB)
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
                        Text(
                            text = "Add to Compare",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddSIPInputField(
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
fun CompareSIPHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF2079EC))
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterStart)
                .padding(start = 8.dp, top = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Compare SIP",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 20.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowScope.SIPTableHeaderCell(
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
fun SIPTableRow(
    entry: SIPTableEntry,
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
            // Monthly Investment - Single line, right-aligned
            SIPTableDataCell(
                text = formatCurrencyWithDecimal(entry.monthlyInvestment),
                weight = 1.2f,
                alignment = TextAlign.End
            )
            
            // Expected Return Rate - Single line, center-aligned
            SIPTableDataCell(
                text = String.format("%.2f", entry.expectedReturnRate),
                weight = 0.8f,
                alignment = TextAlign.Center
            )
            
            // Period - Single line, center-aligned
            SIPTableDataCell(
                text = String.format("%.2f", entry.periodMonths),
                weight = 1f,
                alignment = TextAlign.Center
            )
            
            // Invested Amount - Two lines (integer part and decimal part)
            SIPTableDataCellTwoLines(
                value = entry.investedAmount,
                weight = 1.2f,
                alignment = TextAlign.End
            )
            
            // Estimated Return - Two lines (integer part and decimal part)
            SIPTableDataCellTwoLines(
                value = entry.estimatedReturn,
                weight = 1.2f,
                alignment = TextAlign.End
            )
            
            // Total Value - Two lines (integer part and decimal part)
            SIPTableDataCellTwoLines(
                value = entry.totalValue,
                weight = 1.2f,
                alignment = TextAlign.End
            )
        }
    }
}

@Composable
fun RowScope.SIPTableDataCell(
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
fun RowScope.SIPTableDataCellTwoLines(
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

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


package com.belbytes.calculators.ui.emi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

data class EMIScheduleEntry(
    val month: Int,
    val principal: Double,
    val interest: Double,
    val balance: Double
)

@Composable
fun EMIDetailsScreen(
    emiResult: EMIResult,
    amount: Double,
    interestRate: Double,
    onBackClick: () -> Unit
) {
    val schedule = calculateEMISchedule(
        principal = amount,
        monthlyEMI = emiResult.monthlyEMI,
        interestRate = interestRate,
        totalMonths = emiResult.periodMonths
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        EMIDetailsHeader(onBackClick = onBackClick)

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // EMI Summary Card
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ResultRowBold("Amount", formatCurrencyWithDecimal(amount))
                    ResultRowBold("Interest", String.format("%.2f", interestRate))
                    ResultRowBold("Periods (months)", emiResult.periodMonths.toString())
                    ResultRowBold("Monthly EMI", formatCurrencyWithDecimal(emiResult.monthlyEMI))
                    ResultRowBold("Total Interest", formatCurrencyWithDecimal(emiResult.totalInterest))
                    ResultRowBold("Processing Fees", formatCurrencyWithDecimal(emiResult.processingFees))
                    ResultRowBold("Total Payment", formatCurrencyWithDecimal(emiResult.totalPayment))
                }
            }

            // EMI Schedule Table Card
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
//                        .padding(16.dp)
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2196F3), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TableHeaderCell("Month", weightValue = 1f)
                        TableHeaderCell("Principal", weightValue = 1.5f)
                        TableHeaderCell("Interest", weightValue = 1.5f)
                        TableHeaderCell("Balance", weightValue = 1.5f)
                    }

                    // Table Rows
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        schedule.forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
                                    )
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TableCell(entry.month.toString(), weightValue = 1f)
                                TableCell(formatCurrencyWithDecimal(entry.principal), weightValue = 1.5f)
                                TableCell(formatCurrencyWithDecimal(entry.interest), weightValue = 1.5f)
                                TableCell(formatCurrencyWithDecimal(entry.balance), weightValue = 1.5f)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EMIDetailsHeader(onBackClick: () -> Unit) {
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
            text = "EMI Details",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowScope.TableHeaderCell(text: String, weightValue: Float) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.weight(weightValue),
        textAlign = TextAlign.Center
    )
}

@Composable
fun RowScope.TableCell(text: String, weightValue: Float) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier.weight(weightValue),
        textAlign = TextAlign.Center
    )
}

fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}

@Composable
fun ResultRowBold(label: String, value: String) {
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
            fontWeight = FontWeight.Bold
        )
    }
}

fun calculateEMISchedule(
    principal: Double,
    monthlyEMI: Double,
    interestRate: Double,
    totalMonths: Int
): List<EMIScheduleEntry> {
    val schedule = mutableListOf<EMIScheduleEntry>()
    val monthlyRate = interestRate / (12 * 100)
    var remainingBalance = principal

    for (month in 1..totalMonths) {
        val interestPayment = remainingBalance * monthlyRate
        val principalPayment = monthlyEMI - interestPayment
        remainingBalance -= principalPayment

        schedule.add(
            EMIScheduleEntry(
                month = month,
                principal = principalPayment,
                interest = interestPayment,
                balance = if (remainingBalance > 0) remainingBalance else 0.0
            )
        )
    }

    return schedule
}


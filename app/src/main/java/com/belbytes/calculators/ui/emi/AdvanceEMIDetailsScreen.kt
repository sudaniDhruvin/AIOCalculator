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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.belbytes.calculators.R
import com.belbytes.calculators.utils.formatCurrencyWithDecimal

@Composable
fun AdvanceEMIDetailsScreen(
    emiResult: AdvanceEMIResult,
    amount: Double,
    interestRate: Double,
    interestType: String,
    emiType: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val schedule = calculateAdvanceEMISchedule(
        principal = amount,
        monthlyEMI = emiResult.monthlyEMI,
        interestRate = interestRate,
        totalMonths = emiResult.periodMonths,
        interestType = interestType,
        emiType = emiType
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        AdvanceEMIDetailsHeader(onBackClick = onBackClick)

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
                    ResultRowBold(stringResource(R.string.amount), formatCurrencyWithDecimal(context, amount))
                    ResultRowBold(stringResource(R.string.interest), String.format("%.2f", interestRate))
                    ResultRowBold(stringResource(R.string.periods_months), emiResult.periodMonths.toString())
                    ResultRowBold(stringResource(R.string.monthly_emi), formatCurrencyWithDecimal(context, emiResult.monthlyEMI))
                    ResultRowBold(stringResource(R.string.total_interest), formatCurrencyWithDecimal(context, emiResult.totalInterest))
                    ResultRowBold(stringResource(R.string.processing_fees), formatCurrencyWithDecimal(context, emiResult.processingFees))
                    ResultRowBold(stringResource(R.string.gst_on_interest), formatCurrencyWithDecimal(context, emiResult.gstOnInterest))
                    ResultRowBold(stringResource(R.string.gst_on_processing_fees), formatCurrencyWithDecimal(context, emiResult.gstOnProcessingFees))
                    ResultRowBold(stringResource(R.string.total_payment), formatCurrencyWithDecimal(context, emiResult.totalPayment))
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
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2196F3), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TableHeaderCell(stringResource(R.string.month), weightValue = 1f)
                        TableHeaderCell(stringResource(R.string.principal), weightValue = 1.5f)
                        TableHeaderCell(stringResource(R.string.interest), weightValue = 1.5f)
                        TableHeaderCell(stringResource(R.string.balance), weightValue = 1.5f)
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
                                TableCell(formatCurrencyWithDecimal(context, entry.principal), weightValue = 1.5f)
                                TableCell(formatCurrencyWithDecimal(context, entry.interest), weightValue = 1.5f)
                                TableCell(formatCurrencyWithDecimal(context, entry.balance), weightValue = 1.5f)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdvanceEMIDetailsHeader(onBackClick: () -> Unit) {
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
            text = stringResource(R.string.advance_emi_details),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

fun calculateAdvanceEMISchedule(
    principal: Double,
    monthlyEMI: Double,
    interestRate: Double,
    totalMonths: Int,
    interestType: String,
    emiType: String
): List<EMIScheduleEntry> {
    val schedule = mutableListOf<EMIScheduleEntry>()
    val monthlyRate = interestRate / (12 * 100)
    var remainingBalance = principal

    if (interestType == "Reducing") {
        // Reducing Balance Interest Calculation
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
    } else {
        // Flat Interest Calculation
        // For flat interest, interest is calculated on the original principal for all months
        val monthlyInterest = principal * monthlyRate
        val monthlyPrincipal = monthlyEMI - monthlyInterest

        for (month in 1..totalMonths) {
            remainingBalance -= monthlyPrincipal

            schedule.add(
                EMIScheduleEntry(
                    month = month,
                    principal = monthlyPrincipal,
                    interest = monthlyInterest,
                    balance = if (remainingBalance > 0) remainingBalance else 0.0
                )
            )
        }
    }

    return schedule
}


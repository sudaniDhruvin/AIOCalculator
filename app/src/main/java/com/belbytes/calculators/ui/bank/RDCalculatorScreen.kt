package com.belbytes.calculators.ui.bank

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
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart as MPAndroidPieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlin.math.pow

data class RDResult(
    val totalInvestment: Double,
    val totalInterest: Double,
    val maturityAmount: Double
)

enum class PeriodType {
    YEARS,
    MONTHS
}

@Composable
fun RDCalculatorScreen(
    onBackClick: () -> Unit
) {
    // State variables
    var monthlyAmount by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("") }
    var periodTypeString by rememberSaveable { mutableStateOf(PeriodType.MONTHS.name) }
    val periodType = remember(periodTypeString) { 
        PeriodType.valueOf(periodTypeString)
    }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var rdResult by rememberSaveable { mutableStateOf<RDResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        RDCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Monthly Amount Input
            RDInputField(
                label = "Monthly Amount",
                placeholder = "Ex: 10,000",
                value = monthlyAmount,
                onValueChange = { monthlyAmount = it }
            )

            // Interest % Input
            RDInputField(
                label = "Interest %",
                placeholder = "Ex: 7%",
                value = interestRate,
                onValueChange = { interestRate = it }
            )

            // Period Input
            RDInputField(
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
                RDPeriodTypeRadioButton(
                    label = "Years",
                    selected = periodType == PeriodType.YEARS,
                    onClick = { periodTypeString = PeriodType.YEARS.name }
                )
                RDPeriodTypeRadioButton(
                    label = "Months",
                    selected = periodType == PeriodType.MONTHS,
                    onClick = { periodTypeString = PeriodType.MONTHS.name }
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
                        val result = calculateRD(
                            monthlyAmount,
                            interestRate,
                            period,
                            periodType
                        )
                        if (result != null) {
                            rdResult = result
                            showResults = true
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
                        monthlyAmount = ""
                        interestRate = ""
                        period = ""
                        periodTypeString = PeriodType.MONTHS.name
                        showResults = false
                        rdResult = null
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

            // Results Section
            AnimatedVisibility(
                visible = showResults && rdResult != null,
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
                rdResult?.let { result ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Financial Summary Card
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
                                // Total Investment
                                RDResultRow(
                                    "Total Investment",
                                    formatCurrencyWithDecimal(result.totalInvestment)
                                )
                                
                                // Total Interest
                                RDResultRow(
                                    "Total Interest",
                                    formatCurrencyWithDecimal(result.totalInterest)
                                )
                                
                                // Maturity Amount
                                RDResultRow(
                                    "Maturity Amount",
                                    formatCurrencyWithDecimal(result.maturityAmount)
                                )
                            }
                        }
                        
                        // Donut Chart Card
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
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Donut Chart
                                RDDonutChart(
                                    totalInvestment = result.totalInvestment,
                                    totalInterest = result.totalInterest,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                                
                                // Legend
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Total Investment Legend
                                    RDLegendItem(
                                        label = "Total Investment",
                                        color = Color(0xFF3F6EE4) // Blue - matches pie chart
                                    )
                                    
                                    Spacer(modifier = Modifier.width(24.dp))
                                    
                                    // Total Interest Legend
                                    RDLegendItem(
                                        label = "Total Interest",
                                        color = Color(0xFF00AF52) // Green - matches pie chart
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RDCalculatorHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF4257B2))
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
            text = "RD Calculator",
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
fun RDInputField(
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
fun RDPeriodTypeRadioButton(
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
fun RDResultRow(
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

@Composable
fun RDDonutChart(
    totalInvestment: Double,
    totalInterest: Double,
    modifier: Modifier = Modifier
) {
    val total = totalInvestment + totalInterest
    val investmentPercentage = if (total > 0) (totalInvestment / total * 100).toFloat() else 0f
    val interestPercentage = if (total > 0) (totalInterest / total * 100).toFloat() else 0f

    AndroidView(
        factory = { ctx ->
            MPAndroidPieChart(ctx).apply {
                description.isEnabled = false
                setUsePercentValues(false)
                setDrawEntryLabels(true)
                setEntryLabelTextSize(12f)
                setEntryLabelColor(android.graphics.Color.WHITE)
                setCenterText("")
                setDrawCenterText(false)
                setHoleRadius(50f) // Make it a donut chart (50% hole radius)
                setTransparentCircleRadius(0f)
                rotationAngle = -90f // Start from top
                setRotationEnabled(false)
                
                legend.isEnabled = false // We'll use custom legend
                
                val entries = mutableListOf<PieEntry>()
                entries.add(PieEntry(investmentPercentage, "Total Investment"))
                entries.add(PieEntry(interestPercentage, "Total Interest"))

                val dataSet = PieDataSet(entries, "").apply {
                    colors = listOf(
                        android.graphics.Color.parseColor("#3F6EE4"), // Blue for Investment
                        android.graphics.Color.parseColor("#00AF52")  // Green for Interest
                    )
                    valueTextSize = 14f
                    valueTextColor = android.graphics.Color.WHITE
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return String.format("%.0f", value)
                        }
                    }
                    setDrawValues(true)
                    setYValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                    setXValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                    setSliceSpace(2f)
                }

                data = PieData(dataSet)
                invalidate()
            }
        },
        update = { chart ->
            val entries = mutableListOf<PieEntry>()
            entries.add(PieEntry(investmentPercentage, "Total Investment"))
            entries.add(PieEntry(interestPercentage, "Total Interest"))

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#3F6EE4"), // Blue for Investment
                    android.graphics.Color.parseColor("#00AF52")  // Green for Interest
                )
                valueTextSize = 14f
                valueTextColor = android.graphics.Color.WHITE
                valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.0f", value)
                    }
                }
                setDrawValues(true)
                setYValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                setXValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                setSliceSpace(2f)
            }

            chart.data = PieData(dataSet)
            chart.animateY(1000)
            chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun RDLegendItem(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(50))
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

fun calculateRD(
    monthlyAmount: String,
    interestRate: String,
    period: String,
    periodType: PeriodType
): RDResult? {
    return try {
        val monthly = monthlyAmount.toDoubleOrNull() ?: return null
        val rate = interestRate.toDoubleOrNull() ?: return null
        val periodValue = period.toDoubleOrNull() ?: return null
        
        if (monthly <= 0 || rate <= 0 || periodValue <= 0) return null
        
        // Convert period to months
        val totalMonths = if (periodType == PeriodType.YEARS) {
            (periodValue * 12).toInt()
        } else {
            periodValue.toInt()
        }
        
        // RD Formula: Maturity Amount = P * [((1 + r)^n - 1) / r] * (1 + r)
        // Where P = Monthly Amount, r = Monthly Interest Rate, n = Number of Months
        val monthlyRate = rate / (12 * 100) // Convert annual rate to monthly rate
        
        val maturityAmount = if (monthlyRate > 0) {
            monthly * (((1 + monthlyRate).pow(totalMonths) - 1) / monthlyRate) * (1 + monthlyRate)
        } else {
            monthly * totalMonths
        }
        
        val totalInvestment = monthly * totalMonths
        val totalInterest = maturityAmount - totalInvestment
        
        RDResult(
            totalInvestment = totalInvestment,
            totalInterest = totalInterest,
            maturityAmount = maturityAmount
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


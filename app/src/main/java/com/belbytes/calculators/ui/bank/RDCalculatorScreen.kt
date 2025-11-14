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
    var rdResult by remember { mutableStateOf<RDResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
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
                        text = "Period",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                }
                RDInputField(
                    label = "",
                    placeholder = "Ex: 6",
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
                        when {
                            monthlyAmount.isBlank() || (monthlyAmount.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = "Please enter a valid monthly amount"
                                showResults = false
                                rdResult = null
                            }
                            interestRate.isBlank() || (interestRate.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = "Please enter a valid interest rate"
                                showResults = false
                                rdResult = null
                            }
                            period.isBlank() || (period.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = "Please enter a valid period"
                                showResults = false
                                rdResult = null
                            }
                            else -> {
                                val result = calculateRD(
                                    monthlyAmount,
                                    interestRate,
                                    period,
                                    periodType
                                )
                                if (result != null) {
                                    rdResult = result
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Please check all input values"
                                    showResults = false
                                    rdResult = null
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
                        errorMessage = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEBEBEB)
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
                text = "RD Calculator",
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
fun RDCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "RD Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
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
fun RDPeriodTypeRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() },
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
            try {
                MPAndroidPieChart(ctx).apply {
                description.isEnabled = false
                setUsePercentValues(false)
                setDrawEntryLabels(false) // Remove labels, show only percentages
                setCenterText("")
                setDrawCenterText(false)
                setHoleRadius(50f) // Make it a donut chart (50% hole radius)
                setTransparentCircleRadius(0f)
                rotationAngle = -90f // Start from top
                setRotationEnabled(false)
                
                legend.isEnabled = true
                legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.CENTER
                legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
                legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
                legend.setDrawInside(false)
                legend.textSize = 14f
                legend.textColor = android.graphics.Color.BLACK
                legend.form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
                legend.formSize = 12f
                
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
                            return String.format("%.1f%%", value)
                        }
                    }
                    setDrawValues(true)
                    setYValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                    setSliceSpace(2f)
                }

                data = PieData(dataSet)
                
                // Animate only if attached to window
                post {
                    try {
                        if (isAttachedToWindow && parent != null) {
                            invalidate()
                        }
                    } catch (e: Exception) {
                        // Ignore exceptions during invalidation
                    }
                }
            }
            } catch (e: Exception) {
                // Return a basic chart if initialization fails
                MPAndroidPieChart(ctx)
            }
        },
        update = { chart ->
            try {
                if (chart.isAttachedToWindow && chart.parent != null) {
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
                                return String.format("%.1f%%", value)
                            }
                        }
                        setDrawValues(true)
                        setYValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                        setSliceSpace(2f)
                    }

                    chart.data = PieData(dataSet)
                    chart.animateY(1000)
                    chart.invalidate()
                }
            } catch (e: Exception) {
                // Ignore exceptions during chart updates to prevent crashes
            }
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


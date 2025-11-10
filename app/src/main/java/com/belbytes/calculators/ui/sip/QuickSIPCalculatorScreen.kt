package com.belbytes.calculators.ui.sip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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

data class QuickSIPResult(
    val totalInvestment: Double,
    val estimatedReturns: Double,
    val totalValue: Double,
    val monthlyInvestment: Double = 0.0 // Only for Plan tab
)

@Composable
fun QuickSIPCalculatorScreen(
    onBackClick: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf("SIP") }
    
    // SIP Tab inputs
    var monthlyInvestmentSIP by rememberSaveable { mutableStateOf(1000f) } // Already at 500 increment
    var expReturnRateSIP by rememberSaveable { mutableStateOf(6f) }
    var periodYearsSIP by rememberSaveable { mutableStateOf(8f) }
    
    // Lumpsum Tab inputs
    var totalInvestmentLumpsum by rememberSaveable { mutableStateOf(1000f) } // Already at 500 increment
    var expReturnRateLumpsum by rememberSaveable { mutableStateOf(6f) }
    var periodYearsLumpsum by rememberSaveable { mutableStateOf(8f) }
    
    // Plan Tab inputs
    var targetAmountPlan by rememberSaveable { mutableStateOf(1000f) } // Already at 500 increment
    var expReturnRatePlan by rememberSaveable { mutableStateOf(6f) }
    var periodYearsPlan by rememberSaveable { mutableStateOf(8f) }
    
    val sipResult = remember(
        monthlyInvestmentSIP, expReturnRateSIP, periodYearsSIP,
        totalInvestmentLumpsum, expReturnRateLumpsum, periodYearsLumpsum,
        targetAmountPlan, expReturnRatePlan, periodYearsPlan,
        selectedTab
    ) {
        when (selectedTab) {
            "SIP" -> calculateQuickSIP(
                monthlyInvestmentSIP.toDouble(),
                expReturnRateSIP.toDouble(),
                periodYearsSIP.toInt()
            )
            "Lumpsum" -> calculateQuickLumpsum(
                totalInvestmentLumpsum.toDouble(),
                expReturnRateLumpsum.toDouble(),
                periodYearsLumpsum.toInt()
            )
            "Plan" -> calculateQuickPlan(
                targetAmountPlan.toDouble(),
                expReturnRatePlan.toDouble(),
                periodYearsPlan.toInt()
            )
            else -> null
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        QuickSIPCalculatorHeader(onBackClick = onBackClick)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Tabs
            QuickSIPTabs(
                selectedTab = selectedTab,
                onTabChange = { selectedTab = it }
            )
            
            // Input fields based on selected tab
            when (selectedTab) {
                "SIP" -> {
                    QuickSIPSliderInputField(
                        label = "Monthly Investment",
                        value = monthlyInvestmentSIP,
                        onValueChange = { monthlyInvestmentSIP = it },
                        valueRange = 1000f..100000f,
                        formatValue = { formatCurrency(it.toDouble()) },
                        step = 500f
                    )
                    
                    QuickSIPSliderInputField(
                        label = "Exp. Return Rate (%)",
                        value = expReturnRateSIP,
                        onValueChange = { expReturnRateSIP = it },
                        valueRange = 1f..30f,
                        formatValue = { String.format("%.0f %%", it) }
                    )
                    
                    QuickSIPSliderInputField(
                        label = "Period (Years)",
                        value = periodYearsSIP,
                        onValueChange = { periodYearsSIP = it },
                        valueRange = 1f..50f,
                        formatValue = {
                            val years = it.toInt()
                            val months = (it * 12).toInt()
                            "$years Yr - $months months"
                        }
                    )
                }
                "Lumpsum" -> {
                    QuickSIPSliderInputField(
                        label = "Total Investment",
                        value = totalInvestmentLumpsum,
                        onValueChange = { totalInvestmentLumpsum = it },
                        valueRange = 1000f..100000f,
                        formatValue = { formatCurrency(it.toDouble()) },
                        step = 500f
                    )
                    
                    QuickSIPSliderInputField(
                        label = "Exp. Return Rate (%)",
                        value = expReturnRateLumpsum,
                        onValueChange = { expReturnRateLumpsum = it },
                        valueRange = 1f..30f,
                        formatValue = { String.format("%.0f %%", it) }
                    )
                    
                    QuickSIPSliderInputField(
                        label = "Period (Years)",
                        value = periodYearsLumpsum,
                        onValueChange = { periodYearsLumpsum = it },
                        valueRange = 1f..50f,
                        formatValue = {
                            val years = it.toInt()
                            val months = (it * 12).toInt()
                            "$years Yr - $months months"
                        }
                    )
                }
                "Plan" -> {
                    QuickSIPSliderInputField(
                        label = "Target Amount",
                        value = targetAmountPlan,
                        onValueChange = { targetAmountPlan = it },
                        valueRange = 1000f..100000f,
                        formatValue = { formatCurrency(it.toDouble()) },
                        step = 500f
                    )
                    
                    QuickSIPSliderInputField(
                        label = "Exp. Return Rate (%)",
                        value = expReturnRatePlan,
                        onValueChange = { expReturnRatePlan = it },
                        valueRange = 1f..30f,
                        formatValue = { String.format("%.0f %%", it) }
                    )
                    
                    QuickSIPSliderInputField(
                        label = "Period (Years)",
                        value = periodYearsPlan,
                        onValueChange = { periodYearsPlan = it },
                        valueRange = 1f..50f,
                        formatValue = {
                            val years = it.toInt()
                            val months = (it * 12).toInt()
                            "$years Yr - $months months"
                        }
                    )
                }
            }
            
            // Results Section
            if (sipResult != null) {
                QuickSIPResultsSection(
                    result = sipResult,
                    selectedTab = selectedTab
                )
            }
        }
    }
}

@Composable
fun QuickSIPCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "SIP Calculator",
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
fun QuickSIPTabs(
    selectedTab: String,
    onTabChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        QuickSIPTabButton(
            text = "SIP",
            isSelected = selectedTab == "SIP",
            onClick = { onTabChange("SIP") },
            modifier = Modifier.weight(1f)
        )
        
        QuickSIPTabButton(
            text = "Lumpsum",
            isSelected = selectedTab == "Lumpsum",
            onClick = { onTabChange("Lumpsum") },
            modifier = Modifier.weight(1f)
        )
        
        QuickSIPTabButton(
            text = "Plan",
            isSelected = selectedTab == "Plan",
            onClick = { onTabChange("Plan") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickSIPTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF2079EC) else Color(0xFFEEEEEE)
        ),
        shape = RoundedCornerShape(6.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF222222),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSIPSliderInputField(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    formatValue: (Float) -> String,
    step: Float = 1f
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF222222)
        )
        
        Text(
            text = formatValue(value),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF919191)
        )
        
        var trackWidth by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .onSizeChanged { size ->
                    trackWidth = with(density) { size.width.toDp() }
                }
        ) {
            val progress = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.CenterStart)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(1.dp))
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(2.dp)
                    .align(Alignment.CenterStart)
                    .background(Color(0xFF333333), RoundedCornerShape(1.dp))
            )
            
            if (trackWidth > 0.dp) {
                val thumbOffset = with(density) { (progress * trackWidth.toPx() - 10.dp.toPx()).toDp() }
                Box(
                    modifier = Modifier
                        .offset(x = thumbOffset)
                        .size(20.dp)
                        .align(Alignment.CenterStart)
                        .background(Color.White, CircleShape)
                        .border(1.5.dp, Color(0xFF333333), CircleShape)
                        .pointerInput(Unit) {
                        }
                )
            }
            
            Slider(
                value = value,
                onValueChange = { newValue ->
                    val minValue = valueRange.start
                    val maxValue = valueRange.endInclusive
                    val clampedValue = newValue.coerceIn(minValue, maxValue)
                    // Round to nearest step if step > 1
                    val roundedValue = if (step > 1f) {
                        (kotlin.math.round(clampedValue / step) * step).coerceIn(minValue, maxValue)
                    } else {
                        clampedValue
                    }
                    onValueChange(roundedValue)
                },
                valueRange = valueRange,
                steps = if (step > 1f) {
                    ((valueRange.endInclusive - valueRange.start) / step).toInt() - 1
                } else {
                    0
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun QuickSIPResultsSection(
    result: QuickSIPResult,
    selectedTab: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7F7)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Chart on the left
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuickSIPDonutChart(
                    totalInvestment = result.totalInvestment,
                    estimatedReturns = result.estimatedReturns,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                
                // Legend
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    QuickSIPLegendItem("Total Investment", Color(0xFF3F6EE4))
                    QuickSIPLegendItem("Estimated Return", Color(0xFF00AF52))
                }
            }
            
            // Details on the right
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    "SIP" -> {
                        QuickSIPResultCard("Total Investment", formatCurrencyWithDecimal(result.totalInvestment))
                        QuickSIPResultCard("Estimated Return", formatCurrencyWithDecimal(result.estimatedReturns))
                        QuickSIPResultCard("Total Value", formatCurrencyWithDecimal(result.totalValue), isHighlighted = true)
                    }
                    "Lumpsum" -> {
                        QuickSIPResultCard("Investment Amount", formatCurrencyWithDecimal(result.totalInvestment))
                        QuickSIPResultCard("Estimated Return", formatCurrencyWithDecimal(result.estimatedReturns))
                        QuickSIPResultCard("Total Value", formatCurrencyWithDecimal(result.totalValue), isHighlighted = true)
                    }
                    "Plan" -> {
                        QuickSIPResultCard("Total Investment", formatCurrencyWithDecimal(result.totalInvestment))
                        QuickSIPResultCard("Estimated Return", formatCurrencyWithDecimal(result.estimatedReturns))
                        QuickSIPResultCard("Monthly Investment", formatCurrencyWithDecimal(result.monthlyInvestment), isHighlighted = true)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickSIPResultCard(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = if (isHighlighted) Color(0xFF2079EC) else Color(0xFF333333),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuickSIPDonutChart(
    totalInvestment: Double,
    estimatedReturns: Double,
    modifier: Modifier = Modifier
) {
    val totalValue = totalInvestment + estimatedReturns
    val investmentPercentage = if (totalValue > 0) ((totalInvestment / totalValue) * 100).toFloat() else 0f
    val returnsPercentage = if (totalValue > 0) ((estimatedReturns / totalValue) * 100).toFloat() else 0f
    
    // Ensure percentages sum to 100
    val investmentPercentRounded = investmentPercentage.toInt()
    val returnsPercentRounded = (100 - investmentPercentRounded).coerceAtLeast(0)
    
    AndroidView(
        factory = { ctx ->
            MPAndroidPieChart(ctx).apply {
                description.isEnabled = false
                setUsePercentValues(false)
                setDrawEntryLabels(true)
                setEntryLabelTextSize(14f)
                setEntryLabelColor(android.graphics.Color.WHITE)
                setCenterText("")
                setDrawCenterText(false)
                setHoleRadius(58f)
                setTransparentCircleRadius(61f)
                rotationAngle = -90f
                setRotationEnabled(false)
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = mutableListOf<PieEntry>()
            entries.add(PieEntry(investmentPercentage, "$investmentPercentRounded"))
            entries.add(PieEntry(returnsPercentage, "$returnsPercentRounded"))
            
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#3F6EE4"),
                    android.graphics.Color.parseColor("#00AF52")
                )
                setDrawValues(false)
                valueTextColor = android.graphics.Color.WHITE
                valueTextSize = 14f
            }
            
            chart.data = PieData(dataSet)
            chart.animateY(1000)
            chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun QuickSIPLegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp, 12.dp)
                .background(color, RoundedCornerShape(50))
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF333333),
            maxLines = 1
        )
    }
}

// Calculation functions
fun calculateQuickSIP(
    monthlyInvestment: Double,
    expReturnRate: Double,
    years: Int
): QuickSIPResult? {
    return try {
        if (monthlyInvestment <= 0 || expReturnRate < 0 || years <= 0) return null
        
        val monthlyRate = expReturnRate / (12 * 100)
        val totalMonths = years * 12
        
        val maturityAmount = if (monthlyRate > 0) {
            val growthFactor = java.lang.Math.pow(1 + monthlyRate, totalMonths.toDouble())
            monthlyInvestment * ((growthFactor - 1) / monthlyRate) * (1 + monthlyRate)
        } else {
            monthlyInvestment * totalMonths
        }
        
        val totalInvestment = monthlyInvestment * totalMonths
        val estimatedReturns = maturityAmount - totalInvestment
        
        QuickSIPResult(
            totalInvestment = totalInvestment,
            estimatedReturns = estimatedReturns,
            totalValue = maturityAmount
        )
    } catch (e: Exception) {
        null
    }
}

fun calculateQuickLumpsum(
    totalInvestment: Double,
    expReturnRate: Double,
    years: Int
): QuickSIPResult? {
    return try {
        if (totalInvestment <= 0 || expReturnRate < 0 || years <= 0) return null
        
        val annualRateDecimal = expReturnRate / 100.0
        val totalValue = totalInvestment * java.lang.Math.pow(1 + annualRateDecimal, years.toDouble())
        val estimatedReturns = totalValue - totalInvestment
        
        QuickSIPResult(
            totalInvestment = totalInvestment,
            estimatedReturns = estimatedReturns,
            totalValue = totalValue
        )
    } catch (e: Exception) {
        null
    }
}

fun calculateQuickPlan(
    targetAmount: Double,
    expReturnRate: Double,
    years: Int
): QuickSIPResult? {
    return try {
        if (targetAmount <= 0 || expReturnRate < 0 || years <= 0) return null
        
        val monthlyRate = expReturnRate / (12 * 100)
        val totalMonths = years * 12
        
        // Calculate required monthly SIP to reach target amount
        // Target = Monthly SIP * [((1 + r)^n - 1) / r] * (1 + r)
        // Monthly SIP = Target / ([((1 + r)^n - 1) / r] * (1 + r))
        val monthlyInvestment = if (monthlyRate > 0) {
            val growthFactor = java.lang.Math.pow(1 + monthlyRate, totalMonths.toDouble())
            val factor = ((growthFactor - 1) / monthlyRate) * (1 + monthlyRate)
            targetAmount / factor
        } else {
            targetAmount / totalMonths
        }
        
        val totalInvestment = monthlyInvestment * totalMonths
        val estimatedReturns = targetAmount - totalInvestment
        
        QuickSIPResult(
            totalInvestment = totalInvestment,
            estimatedReturns = estimatedReturns,
            totalValue = targetAmount,
            monthlyInvestment = monthlyInvestment
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrency(amount: Double): String {
    return String.format("₹ %,.0f", amount)
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


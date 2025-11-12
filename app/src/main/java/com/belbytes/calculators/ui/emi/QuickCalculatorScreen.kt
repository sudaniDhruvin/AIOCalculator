package com.belbytes.calculators.ui.emi

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
import com.belbytes.calculators.ads.BannerAd
import com.belbytes.calculators.ads.NativeAd
import com.github.mikephil.charting.charts.PieChart as MPAndroidPieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.roundToInt

@Composable
fun QuickCalculatorScreen(
    onBackClick: () -> Unit
) {
    var selectedCategory by rememberSaveable { mutableStateOf("EMI") }
    
    var amountEMI by rememberSaveable { mutableStateOf(100000f) }
    var interestRateEMI by rememberSaveable { mutableStateOf(1.0f) }
    var periodYearsEMI by rememberSaveable { mutableStateOf(1f) }
    
    var amountAmount by rememberSaveable { mutableStateOf(100000f) }
    var interestRateAmount by rememberSaveable { mutableStateOf(1.0f) }
    var monthlyEMI by rememberSaveable { mutableStateOf(1000f) }
    
    val emiResult = remember(amountEMI, interestRateEMI, periodYearsEMI, amountAmount, interestRateAmount, monthlyEMI, selectedCategory) {
        if (selectedCategory == "EMI") {
            calculateQuickEMI(amountEMI.toDouble(), interestRateEMI.toDouble(), periodYearsEMI.toInt())
        } else {
            calculateQuickAmountFromInputs(amountAmount.toDouble(), interestRateAmount.toDouble(), monthlyEMI.toDouble())
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        QuickCalculatorHeader(onBackClick = onBackClick)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Banner Ad
            BannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            CategoryToggle(
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it }
            )
            
            if (selectedCategory == "EMI") {
                SliderInputField(
                    label = "Amount",
                    value = amountEMI,
                    onValueChange = { amountEMI = it },
                    valueRange = 100000f..10000000f,
                    formatValue = { formatCurrency(it.toDouble()) }
                )
                
                SliderInputField(
                    label = "Interest (%)",
                    value = interestRateEMI,
                    onValueChange = { interestRateEMI = it },
                    valueRange = 1.0f..30.0f,
                    formatValue = { String.format("%.1f %%", it) }
                )
                
                SliderInputField(
                    label = "Period (Years)",
                    value = periodYearsEMI,
                    onValueChange = { periodYearsEMI = it },
                    valueRange = 1f..100f,
                    formatValue = { 
                        val years = it.toInt()
                        val months = (it * 12).toInt()
                        "$years Yr - $months months"
                    }
                )
            } else {
                SliderInputField(
                    label = "Amount",
                    value = amountAmount,
                    onValueChange = { amountAmount = it },
                    valueRange = 100000f..10000000f,
                    formatValue = { formatCurrency(it.toDouble()) }
                )
                
                SliderInputField(
                    label = "Interest (%)",
                    value = interestRateAmount,
                    onValueChange = { interestRateAmount = it },
                    valueRange = 1.0f..30.0f,
                    formatValue = { String.format("%.1f %%", it) }
                )
                
                SliderInputField(
                    label = "Monthly EMI",
                    value = monthlyEMI,
                    onValueChange = { monthlyEMI = it },
                    valueRange = 1000f..100000f,
                    formatValue = { formatCurrency(it.toDouble()) }
                )
            }
            
            if (emiResult != null) {
                ResultsSection(
                    emiResult = emiResult,
                    key = selectedCategory
                )
            }
            
            // Native Ad
            NativeAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun QuickCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "Quick Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CategoryToggle(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CategoryButton(
            text = "EMI",
            isSelected = selectedCategory == "EMI",
            onClick = { onCategoryChange("EMI") },
            modifier = Modifier.weight(1f)
        )
        
        CategoryButton(
            text = "Amount",
            isSelected = selectedCategory == "Amount",
            onClick = { onCategoryChange("Amount") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryButton(
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
fun SliderInputField(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    formatValue: (Float) -> String
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
                    onValueChange(clampedValue)
                },
                valueRange = valueRange,
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
fun ResultsSection(emiResult: QuickEMIResult, key: String = "") {
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
                // Use key to force chart re-render when switching modes
                key(key, emiResult.principal, emiResult.totalInterest) {
                    QuickPieChart(
                        principal = emiResult.principal,
                        interest = emiResult.totalInterest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
                
                // Legend
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LegendItem("Principal", Color(0xFF3F6EE4))
                    LegendItem("Interest", Color(0xFF00AF52))
                }
            }
            
            // Details on the right
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickResultRow("Total Interest", formatCurrency(emiResult.totalInterest))
                QuickResultRow("Total Payment", formatCurrency(emiResult.totalPayment))
                QuickResultRow("Monthly EMI", formatCurrency(emiResult.monthlyEMI))
            }
        }
    }
}

@Composable
fun QuickResultRow(label: String, value: String) {
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
            color = Color(0xFF333333),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuickPieChart(
    principal: Double,
    interest: Double,
    modifier: Modifier = Modifier
) {
    val total = principal + interest
    val principalPercentage = (principal / total * 100).toFloat()
    val interestPercentage = (interest / total * 100).toFloat()
    
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
                setHoleRadius(50f)
                setTransparentCircleRadius(0f)
                rotationAngle = -90f
                setRotationEnabled(false)
                
                legend.isEnabled = false
            }
        },
        update = { chart ->
            // Update chart data when principal or interest changes
            val entries = mutableListOf<PieEntry>()
            entries.add(PieEntry(principalPercentage, "Principal"))
            entries.add(PieEntry(interestPercentage, "Interest"))
            
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#3F6EE4"),
                    android.graphics.Color.parseColor("#00AF52")
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
fun LegendItem(label: String, color: Color) {
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
            color = Color(0xFF333333)
        )
    }
}

// Data class for Quick EMI Result
data class QuickEMIResult(
    val monthlyEMI: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val principal: Double
)

// Calculation functions
fun calculateQuickEMI(
    amount: Double,
    interestRate: Double,
    years: Int
): QuickEMIResult? {
    return try {
        val months = years * 12
        val monthlyRate = interestRate / (12 * 100)
        
        val monthlyEMI = if (monthlyRate > 0) {
            val rateFactor = java.lang.Math.pow(1 + monthlyRate, months.toDouble())
            amount * monthlyRate * rateFactor / (rateFactor - 1)
        } else {
            amount / months
        }
        
        val totalPayment = monthlyEMI * months
        val totalInterest = totalPayment - amount
        
        QuickEMIResult(
            monthlyEMI = monthlyEMI,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principal = amount
        )
    } catch (e: Exception) {
        null
    }
}

fun calculateQuickAmount(
    monthlyEMI: Double,
    interestRate: Double,
    years: Int
): QuickEMIResult? {
    return try {
        val months = years * 12
        val monthlyRate = interestRate / (12 * 100)
        
        val principal = if (monthlyRate > 0) {
            monthlyEMI * (1 - java.lang.Math.pow(1 + monthlyRate, -months.toDouble())) / monthlyRate
        } else {
            monthlyEMI * months
        }
        
        val totalPayment = monthlyEMI * months
        val totalInterest = totalPayment - principal
        
        QuickEMIResult(
            monthlyEMI = monthlyEMI,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principal = principal
        )
    } catch (e: Exception) {
        null
    }
}

fun calculateQuickAmountFromInputs(
    amount: Double,
    interestRate: Double,
    monthlyEMI: Double
): QuickEMIResult? {
    return try {
        val monthlyRate = interestRate / (12 * 100)
        
        // Calculate the number of months (period) from Amount, Interest, and Monthly EMI
        val months = if (monthlyRate > 0 && monthlyEMI > amount * monthlyRate) {
            ceil(
                -ln(1 - (amount * monthlyRate) / monthlyEMI) /
                        ln(1 + monthlyRate)
            ).toInt()
        } else if (monthlyEMI > 0) {
            (amount / monthlyEMI).toInt()
        } else {
            return null
        }
        
        val totalPayment = monthlyEMI * months
        val totalInterest = totalPayment - amount
        
        QuickEMIResult(
            monthlyEMI = monthlyEMI,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principal = amount
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrency(amount: Double): String {
    return String.format("%,.2f", amount)
}


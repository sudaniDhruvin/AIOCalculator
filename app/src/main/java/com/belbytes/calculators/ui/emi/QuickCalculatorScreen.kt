package com.belbytes.calculators.ui.emi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.belbytes.calculators.R
import android.view.View
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
    val context = LocalContext.current
    val emiLabel = context.getString(R.string.emi)
    val amountLabel = context.getString(R.string.amount)
    val categories = listOf(emiLabel, amountLabel)
    val pagerState = rememberPagerState(initialPage = 0) { categories.size }
    var selectedCategory by rememberSaveable { mutableStateOf(emiLabel) }
    
    var amountEMI by rememberSaveable { mutableStateOf(100000f) }
    var interestRateEMI by rememberSaveable { mutableStateOf(1.0f) }
    var periodYearsEMI by rememberSaveable { mutableStateOf(1f) }
    
    var amountAmount by rememberSaveable { mutableStateOf(100000f) }
    var interestRateAmount by rememberSaveable { mutableStateOf(1.0f) }
    var monthlyEMI by rememberSaveable { mutableStateOf(1000f) }
    
    // Sync selectedCategory with pager state
    LaunchedEffect(pagerState.currentPage) {
        selectedCategory = categories[pagerState.currentPage]
    }
    
    val emiResult = remember(amountEMI, interestRateEMI, periodYearsEMI, amountAmount, interestRateAmount, monthlyEMI, selectedCategory) {
        if (selectedCategory == emiLabel) {
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
                .imePadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Banner Ad
            BannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            val scope = rememberCoroutineScope()
            CategoryToggle(
                selectedCategory = selectedCategory,
                pagerState = pagerState,
                onCategoryChange = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
            
            // Swipeable content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (categories[page] == emiLabel) {
                        SliderInputField(
                            label = context.getString(R.string.amount),
                            value = amountEMI,
                            onValueChange = { amountEMI = it },
                            valueRange = 100000f..10000000f,
                            formatValue = { formatCurrency(it.toDouble()) },
                            step = 50000f
                        )
                        
                        SliderInputField(
                            label = context.getString(R.string.interest_rate),
                            value = interestRateEMI,
                            onValueChange = { interestRateEMI = it },
                            valueRange = 1.0f..30.0f,
                            formatValue = { String.format("%.1f %%", it) }
                        )
                        
                        SliderInputField(
                            label = context.getString(R.string.period_years),
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
                            label = context.getString(R.string.amount),
                            value = amountAmount,
                            onValueChange = { amountAmount = it },
                            valueRange = 100000f..10000000f,
                            formatValue = { formatCurrency(it.toDouble()) },
                            step = 50000f
                        )
                        
                        SliderInputField(
                            label = context.getString(R.string.interest_rate),
                            value = interestRateAmount,
                            onValueChange = { interestRateAmount = it },
                            valueRange = 1.0f..30.0f,
                            formatValue = { String.format("%.1f %%", it) }
                        )
                        
                        SliderInputField(
                            label = context.getString(R.string.monthly_emi),
                            value = monthlyEMI,
                            onValueChange = { monthlyEMI = it },
                            valueRange = 1000f..100000f,
                            formatValue = { formatCurrency(it.toDouble()) }
                        )
                    }
                }
            }
            
            // Results Section
            emiResult?.let { result ->
                ResultsSection(
                    emiResult = result,
                    key = selectedCategory
                )
            } ?: run {
                // Show error message if calculation failed
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = context.getString(R.string.error_check_inputs),
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFC62828),
                        fontSize = 14.sp
                    )
                }
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
    val context = LocalContext.current
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
                contentDescription = context.getString(R.string.back),
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Text(
            text = context.getString(R.string.quick_calculator),
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
    pagerState: PagerState,
    onCategoryChange: (Int) -> Unit
) {
    val categories = listOf("EMI", "Amount")
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        categories.forEachIndexed { index, category ->
            CategoryButton(
                text = category,
                selected = selectedCategory == category,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CategoryButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color(0xFF2196F3) else Color(0xFF757575),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(0xFF2196F3))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderInputField(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    formatValue: (Float) -> String,
    step: Float? = null
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
            )
            }
            
            val steps = step?.let { 
                ((valueRange.endInclusive - valueRange.start) / it).toInt() - 1
            }
            
            Slider(
                value = value,
                onValueChange = { newValue ->
                    val minValue = valueRange.start
                    val maxValue = valueRange.endInclusive
                    val clampedValue = newValue.coerceIn(minValue, maxValue)
                    
                    // Snap to step if step is specified
                    val finalValue = if (step != null && step > 0) {
                        val steppedValue = ((clampedValue - minValue) / step).roundToInt() * step + minValue
                        steppedValue.coerceIn(minValue, maxValue)
                    } else {
                        clampedValue
                    }
                    
                    onValueChange(finalValue)
                },
                valueRange = valueRange,
                steps = steps ?: 0,
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
    // Handle edge cases: prevent division by zero and negative values
    val safePrincipal = principal.coerceAtLeast(0.0)
    val safeInterest = interest.coerceAtLeast(0.0)
    val total = safePrincipal + safeInterest
    
    // Calculate percentages safely
    val principalPercentage = if (total > 0) {
        (safePrincipal / total * 100).toFloat()
    } else {
        0f
    }
    val interestPercentage = if (total > 0) {
        (safeInterest / total * 100).toFloat()
    } else {
        0f
    }
    
    AndroidView(
        factory = { ctx ->
            try {
                MPAndroidPieChart(ctx).apply {
                    // Configure chart appearance - Donut chart
                    description.isEnabled = false
                    setUsePercentValues(false)
                    setDrawEntryLabels(false) // Remove labels, show only percentages
                    setCenterText("")
                    setDrawCenterText(false)
                    setHoleRadius(50f) // Make it a donut chart (50% hole radius)
                    setTransparentCircleRadius(0f)
                    rotationAngle = -90f // Start from top
                    setRotationEnabled(false)
                    setExtraOffsets(5f, 5f, 5f, 5f)
                    isHighlightPerTapEnabled = false
                    
                    // Disable legend since we show it separately in ResultsSection
                    legend.isEnabled = false
                    
                    // Ensure chart is visible
                    visibility = View.VISIBLE
                    
                    // Set up initial data if we have valid values
                    if (total > 0) {
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
                                    return String.format("%.1f%%", value)
                                }
                            }
                            setDrawValues(true)
                            setYValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                            setSliceSpace(2f)
                        }
                        
                        data = PieData(dataSet)
                        
                        // Animate when chart is ready
                        post {
                            try {
                                if (isAttachedToWindow && parent != null) {
                                    animateY(1000)
                                    invalidate()
                                }
                            } catch (e: Exception) {
                                // Ignore exceptions during animation
                            }
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
                // Update chart data when principal or interest changes
                if (total > 0) {
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
                                return String.format("%.1f%%", value)
                            }
                        }
                        setDrawValues(true)
                        setYValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                        setSliceSpace(2f)
                    }
                    
                    chart.data = PieData(dataSet)
                    chart.notifyDataSetChanged()
                    
                    // Animate update if chart is ready
                    chart.post {
                        try {
                            if (chart.isAttachedToWindow && chart.parent != null) {
                                chart.animateY(1000)
                                chart.invalidate()
                            }
                        } catch (e: Exception) {
                            // Ignore exceptions during animation
                        }
                    }
                } else {
                    // Clear chart if no valid data
                    chart.data = null
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
        // Validate inputs
        if (amount <= 0 || interestRate < 0 || years <= 0 || years > 100) {
            return null
        }
        
        val months = years * 12
        if (months <= 0) return null
        
        val monthlyRate = interestRate / (12 * 100)
        
        val monthlyEMI = if (monthlyRate > 0) {
            val rateFactor = java.lang.Math.pow(1 + monthlyRate, months.toDouble())
            if (rateFactor <= 1) return null // Prevent division by zero
            amount * monthlyRate * rateFactor / (rateFactor - 1)
        } else {
            amount / months
        }
        
        // Validate calculated EMI
        if (monthlyEMI.isNaN() || monthlyEMI.isInfinite() || monthlyEMI <= 0) {
            return null
        }
        
        val totalPayment = monthlyEMI * months
        val totalInterest = totalPayment - amount
        
        QuickEMIResult(
            monthlyEMI = monthlyEMI,
            totalInterest = totalInterest.coerceAtLeast(0.0),
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
        // Validate inputs
        if (amount <= 0 || interestRate < 0 || monthlyEMI <= 0) {
            return null
        }
        
        val monthlyRate = interestRate / (12 * 100)
        
        // Calculate the number of months (period) from Amount, Interest, and Monthly EMI
        val monthsNullable: Int? = if (monthlyRate > 0 && monthlyEMI > amount * monthlyRate) {
            val calculatedMonths = ceil(
                -ln(1 - (amount * monthlyRate) / monthlyEMI) /
                        ln(1 + monthlyRate)
            ).toInt()
            // Validate calculated months
            if (calculatedMonths <= 0 || calculatedMonths > 1200) null else calculatedMonths
        } else if (monthlyEMI > 0) {
            val calculatedMonths = (amount / monthlyEMI).toInt()
            if (calculatedMonths <= 0 || calculatedMonths > 1200) null else calculatedMonths
        } else {
            null
        }
        
        val months: Int = monthsNullable ?: return null
        val monthsDouble = months.toDouble()
        val totalPayment = monthlyEMI * monthsDouble
        val totalInterest = totalPayment - amount
        
        // Validate results
        if (totalPayment.isNaN() || totalPayment.isInfinite() || 
            totalInterest.isNaN() || totalInterest.isInfinite()) {
            return null
        }
        
        QuickEMIResult(
            monthlyEMI = monthlyEMI,
            totalInterest = totalInterest.coerceAtLeast(0.0),
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


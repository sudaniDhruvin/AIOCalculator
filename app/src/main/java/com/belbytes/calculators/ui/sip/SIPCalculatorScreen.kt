package com.belbytes.calculators.ui.sip

import android.os.Parcelable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
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
import android.view.View
import com.belbytes.calculators.ads.BannerAd
import com.belbytes.calculators.ads.NativeAd
import com.github.mikephil.charting.charts.PieChart as MPAndroidPieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.parcelize.Parcelize

@Parcelize
data class SIPCalculatorResult(
    val totalInvestment: Double,
    val estimatedReturns: Double,
    val totalValue: Double,
    val monthlyInvestment: Double = 0.0 // Only for Plan tab
) : Parcelable

@Composable
fun SIPCalculatorScreen(
    onBackClick: () -> Unit
) {
    val tabs = listOf("SIP", "Lumpsum", "Plan")
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    var selectedTab by rememberSaveable { mutableStateOf("SIP") }
    var resultTab by rememberSaveable { mutableStateOf<String?>(null) } // Track which tab the result belongs to
    
    // SIP Tab inputs
    var monthlyInvestmentSIP by rememberSaveable { mutableStateOf("") }
    var expReturnRateSIP by rememberSaveable { mutableStateOf("") }
    var periodYearsSIP by rememberSaveable { mutableStateOf("") }
    
    // Lumpsum Tab inputs
    var totalInvestmentLumpsum by rememberSaveable { mutableStateOf("") }
    var expReturnRateLumpsum by rememberSaveable { mutableStateOf("") }
    var periodYearsLumpsum by rememberSaveable { mutableStateOf("") }
    
    // Plan Tab inputs
    var targetAmountPlan by rememberSaveable { mutableStateOf("") }
    var expReturnRatePlan by rememberSaveable { mutableStateOf("") }
    var periodYearsPlan by rememberSaveable { mutableStateOf("") }
    
    var showResults by rememberSaveable { mutableStateOf(false) }
    var sipResult by remember { mutableStateOf<SIPCalculatorResult?>(null) }
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
    
    // Sync selectedTab with pager state
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = tabs[pagerState.currentPage]
        showResults = false
        sipResult = null
        resultTab = null
        errorMessage = null
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SIPCalculatorHeader(onBackClick = onBackClick)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Banner Ad
            BannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            // Tabs
            val scope = rememberCoroutineScope()
            SIPCalculatorTabs(
                selectedTab = selectedTab,
                pagerState = pagerState,
                onTabChange = { index ->
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Input fields based on selected tab
                    when (tabs[page]) {
                        "SIP" -> {
                            SIPCalculatorInputField(
                                label = "Monthly Investment",
                                placeholder = "Ex: 1000",
                                value = monthlyInvestmentSIP,
                                onValueChange = { monthlyInvestmentSIP = it }
                            )
                            
                            SIPCalculatorInputField(
                                label = "Exp. Return Rate (%)",
                                placeholder = "Ex: 12",
                                value = expReturnRateSIP,
                                onValueChange = { expReturnRateSIP = it }
                            )
                            
                            SIPCalculatorInputField(
                                label = "Period (Years)",
                                placeholder = "Ex: 10",
                                value = periodYearsSIP,
                                onValueChange = { periodYearsSIP = it }
                            )
                        }
                        "Lumpsum" -> {
                            SIPCalculatorInputField(
                                label = "Total Investment",
                                placeholder = "Ex: 1000",
                                value = totalInvestmentLumpsum,
                                onValueChange = { totalInvestmentLumpsum = it }
                            )
                            
                            SIPCalculatorInputField(
                                label = "Exp. Return Rate (%)",
                                placeholder = "Ex: 12",
                                value = expReturnRateLumpsum,
                                onValueChange = { expReturnRateLumpsum = it }
                            )
                            
                            SIPCalculatorInputField(
                                label = "Period (Years)",
                                placeholder = "Ex: 10",
                                value = periodYearsLumpsum,
                                onValueChange = { periodYearsLumpsum = it }
                            )
                        }
                        "Plan" -> {
                            SIPCalculatorInputField(
                                label = "Target Investment",
                                placeholder = "Ex: 1000",
                                value = targetAmountPlan,
                                onValueChange = { targetAmountPlan = it }
                            )
                            
                            SIPCalculatorInputField(
                                label = "Exp. Return Rate (%)",
                                placeholder = "Ex: 12",
                                value = expReturnRatePlan,
                                onValueChange = { expReturnRatePlan = it }
                            )
                            
                            SIPCalculatorInputField(
                                label = "Period (Years)",
                                placeholder = "Ex: 10",
                                value = periodYearsPlan,
                                onValueChange = { periodYearsPlan = it }
                            )
                        }
                    }
                }
            }
            
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calculate Button
                Button(
                    onClick = {
                        keyboardController?.hide() // Hide keyboard when calculate is clicked
                        errorMessage = null
                        val result = when (selectedTab) {
                            "SIP" -> calculateSIP(
                                monthlyInvestmentSIP,
                                expReturnRateSIP,
                                periodYearsSIP
                            )
                            "Lumpsum" -> calculateLumpsumSIP(
                                totalInvestmentLumpsum,
                                expReturnRateLumpsum,
                                periodYearsLumpsum
                            )
                            "Plan" -> calculatePlanSIP(
                                targetAmountPlan,
                                expReturnRatePlan,
                                periodYearsPlan
                            )
                            else -> null
                        }
                        if (result != null) {
                            sipResult = result
                            resultTab = selectedTab
                            showResults = true
                            errorMessage = null
                        } else {
                            showResults = false
                            sipResult = null
                            errorMessage = when (selectedTab) {
                                "SIP" -> when {
                                    monthlyInvestmentSIP.isBlank() || (monthlyInvestmentSIP.toDoubleOrNull() ?: -1.0) <= 0 ->
                                        "Please enter a valid monthly investment"
                                    expReturnRateSIP.isBlank() || expReturnRateSIP.toDoubleOrNull() == null ->
                                        "Please enter a valid expected return rate"
                                    periodYearsSIP.isBlank() || (periodYearsSIP.toDoubleOrNull() ?: -1.0) <= 0 ->
                                        "Please enter a valid period in years"
                                    else -> "Please check all input values"
                                }
                                "Lumpsum" -> when {
                                    totalInvestmentLumpsum.isBlank() || (totalInvestmentLumpsum.toDoubleOrNull() ?: -1.0) <= 0 ->
                                        "Please enter a valid total investment"
                                    expReturnRateLumpsum.isBlank() || expReturnRateLumpsum.toDoubleOrNull() == null ->
                                        "Please enter a valid expected return rate"
                                    periodYearsLumpsum.isBlank() || (periodYearsLumpsum.toDoubleOrNull() ?: -1.0) <= 0 ->
                                        "Please enter a valid period in years"
                                    else -> "Please check all input values"
                                }
                                "Plan" -> when {
                                    targetAmountPlan.isBlank() || (targetAmountPlan.toDoubleOrNull() ?: -1.0) <= 0 ->
                                        "Please enter a valid target amount"
                                    expReturnRatePlan.isBlank() || expReturnRatePlan.toDoubleOrNull() == null ->
                                        "Please enter a valid expected return rate"
                                    periodYearsPlan.isBlank() || (periodYearsPlan.toDoubleOrNull() ?: -1.0) <= 0 ->
                                        "Please enter a valid period in years"
                                    else -> "Please check all input values"
                                }
                                else -> "Please check all input values"
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
                        when (selectedTab) {
                            "SIP" -> {
                                monthlyInvestmentSIP = ""
                                expReturnRateSIP = ""
                                periodYearsSIP = ""
                            }
                            "Lumpsum" -> {
                                totalInvestmentLumpsum = ""
                                expReturnRateLumpsum = ""
                                periodYearsLumpsum = ""
                            }
                            "Plan" -> {
                                targetAmountPlan = ""
                                expReturnRatePlan = ""
                                periodYearsPlan = ""
                            }
                        }
                        showResults = false
                        sipResult = null
                        resultTab = null
                        errorMessage = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEBEBEB)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Reset",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }
            
            // Error Message Section
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
                        fontSize = 14.sp,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Results Section - Only show if results belong to current tab
            AnimatedVisibility(
                visible = showResults && sipResult != null && resultTab == selectedTab,
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
                sipResult?.let { result ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Horizontal Divider
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Results Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF7F7F7)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                when (selectedTab) {
                                    "SIP" -> {
                                        SIPCalculatorResultRow("Total Investment", formatCurrencyWithDecimal(result.totalInvestment))
                                        SIPCalculatorResultRow("Estimated Returns", formatCurrencyWithDecimal(result.estimatedReturns))
                                        SIPCalculatorResultRow("Total Value", formatCurrencyWithDecimal(result.totalValue))
                                    }
                                    "Lumpsum" -> {
                                        SIPCalculatorResultRow("Invested Amount", formatCurrencyWithDecimal(result.totalInvestment))
                                        SIPCalculatorResultRow("Estimated Returns", formatCurrencyWithDecimal(result.estimatedReturns))
                                        SIPCalculatorResultRow("Total Value", formatCurrencyWithDecimal(result.totalValue))
                                    }
                                    "Plan" -> {
                                        SIPCalculatorResultRow("Total Investment", formatCurrencyWithDecimal(result.totalInvestment))
                                        SIPCalculatorResultRow("Estimated Returns", formatCurrencyWithDecimal(result.estimatedReturns))
                                        SIPCalculatorResultRow("Monthly Investment", formatCurrencyWithDecimal(result.monthlyInvestment))
                                    }
                                }
                            }
                        }
                        
                        // Donut Chart
                        SIPCalculatorDonutChart(
                            totalInvestment = result.totalInvestment,
                            estimatedReturns = result.estimatedReturns,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                        
                        // Legend
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            SIPCalculatorLegendItem("Total Investment", Color(0xFF3F6EE4))
                            SIPCalculatorLegendItem("Estimated Return", Color(0xFF00AF52))
                        }
                    }
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
fun SIPCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "SIP Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SIPCalculatorTabs(
    selectedTab: String,
    pagerState: PagerState,
    onTabChange: (Int) -> Unit
) {
    val tabs = listOf("SIP", "Lumpsum", "Plan")
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            SIPCalculatorTabButton(
                text = tab,
                selected = selectedTab == tab,
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
fun SIPCalculatorTabButton(
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

@Composable
fun SIPCalculatorInputField(
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
fun SIPCalculatorResultRow(
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
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun SIPCalculatorDonutChart(
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
            try {
                MPAndroidPieChart(ctx).apply {
                    description.isEnabled = false
                    setUsePercentValues(false)
                    setDrawEntryLabels(false) // Remove labels, show only percentages
                    setCenterText("")
                    setDrawCenterText(false)
                    setHoleRadius(58f)
                    setTransparentCircleRadius(61f)
                    rotationAngle = -90f
                    setRotationEnabled(false)
                    legend.isEnabled = false // Disable built-in legend since we have custom legend below
                }
            } catch (e: Exception) {
                // Return a basic chart if initialization fails
                MPAndroidPieChart(ctx)
            }
        },
        update = { chart ->
            try {
                // Ensure we have valid data
                if (totalValue > 0 && investmentPercentage >= 0 && returnsPercentage >= 0) {
                    val entries = mutableListOf<PieEntry>()
                    entries.add(PieEntry(investmentPercentage, "Total Investment"))
                    entries.add(PieEntry(returnsPercentage, "Estimated Returns"))
                    
                    val dataSet = PieDataSet(entries, "").apply {
                        colors = listOf(
                            android.graphics.Color.parseColor("#3F6EE4"),
                            android.graphics.Color.parseColor("#00AF52")
                        )
                        setDrawValues(true)
                        valueTextColor = android.graphics.Color.WHITE
                        valueTextSize = 14f
                        valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return String.format("%.1f%%", value)
                            }
                        }
                        setYValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                        setSliceSpace(2f) // Add spacing between segments
                    }
                    
                    chart.data = PieData(dataSet)
                    chart.notifyDataSetChanged()
                    chart.invalidate()
                    
                    // Ensure chart is visible and properly sized
                    chart.visibility = View.VISIBLE
                    
                    chart.post {
                        try {
                            if (chart.data != null && chart.data!!.entryCount > 0) {
                                chart.animateY(1000)
                            }
                        } catch (e: Exception) {
                            // Fallback: just invalidate if animation fails
                            chart.invalidate()
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore exceptions during chart updates to prevent crashes
            }
        },
        modifier = modifier
    )
}

@Composable
fun SIPCalculatorLegendItem(label: String, color: Color) {
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
fun calculateSIP(
    monthlyInvestment: String,
    expReturnRate: String,
    periodYears: String
): SIPCalculatorResult? {
    return try {
        val monthly = monthlyInvestment.toDoubleOrNull() ?: return null
        val rate = expReturnRate.toDoubleOrNull() ?: return null
        val years = periodYears.toDoubleOrNull()?.toInt() ?: return null
        
        if (monthly <= 0 || rate < 0 || years <= 0) return null
        
        val monthlyRate = rate / (12 * 100)
        val totalMonths = years * 12
        
        val maturityAmount = if (monthlyRate > 0) {
            val growthFactor = java.lang.Math.pow(1 + monthlyRate, totalMonths.toDouble())
            monthly * ((growthFactor - 1) / monthlyRate) * (1 + monthlyRate)
        } else {
            monthly * totalMonths
        }
        
        val totalInvestment = monthly * totalMonths
        val estimatedReturns = maturityAmount - totalInvestment
        
        SIPCalculatorResult(
            totalInvestment = totalInvestment,
            estimatedReturns = estimatedReturns,
            totalValue = maturityAmount
        )
    } catch (e: Exception) {
        null
    }
}

fun calculateLumpsumSIP(
    totalInvestment: String,
    expReturnRate: String,
    periodYears: String
): SIPCalculatorResult? {
    return try {
        val investment = totalInvestment.toDoubleOrNull() ?: return null
        val rate = expReturnRate.toDoubleOrNull() ?: return null
        val years = periodYears.toDoubleOrNull()?.toInt() ?: return null
        
        if (investment <= 0 || rate < 0 || years <= 0) return null
        
        val annualRateDecimal = rate / 100.0
        val totalValue = investment * java.lang.Math.pow(1 + annualRateDecimal, years.toDouble())
        val estimatedReturns = totalValue - investment
        
        SIPCalculatorResult(
            totalInvestment = investment,
            estimatedReturns = estimatedReturns,
            totalValue = totalValue
        )
    } catch (e: Exception) {
        null
    }
}

fun calculatePlanSIP(
    targetAmount: String,
    expReturnRate: String,
    periodYears: String
): SIPCalculatorResult? {
    return try {
        val target = targetAmount.toDoubleOrNull() ?: return null
        val rate = expReturnRate.toDoubleOrNull() ?: return null
        val years = periodYears.toDoubleOrNull()?.toInt() ?: return null
        
        if (target <= 0 || rate < 0 || years <= 0) return null
        
        val monthlyRate = rate / (12 * 100)
        val totalMonths = years * 12
        
        // Calculate required monthly SIP to reach target amount
        val monthlyInvestment = if (monthlyRate > 0) {
            val growthFactor = java.lang.Math.pow(1 + monthlyRate, totalMonths.toDouble())
            val factor = ((growthFactor - 1) / monthlyRate) * (1 + monthlyRate)
            target / factor
        } else {
            target / totalMonths
        }
        
        val totalInvestment = monthlyInvestment * totalMonths
        val estimatedReturns = target - totalInvestment
        
        SIPCalculatorResult(
            totalInvestment = totalInvestment,
            estimatedReturns = estimatedReturns,
            totalValue = target,
            monthlyInvestment = monthlyInvestment
        )
    } catch (e: Exception) {
        null
    }
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


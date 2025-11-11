package com.belbytes.calculators.ui.sip

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

data class AdvanceSIPResult(
    val totalInvestment: Double,
    val estimatedReturns: Double,
    val totalValue: Double
)

@Composable
fun AdvanceSIPCalculatorScreen(
    onBackClick: () -> Unit
) {
    // State variables
    var initialInvestmentEnabled by rememberSaveable { mutableStateOf(true) }
    var initialInvestment by rememberSaveable { mutableStateOf("") }
    var monthlyInvestment by rememberSaveable { mutableStateOf("") }
    var expReturnRate by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("") }
    var isPeriodYears by rememberSaveable { mutableStateOf(true) }
    var inflationRateEnabled by rememberSaveable { mutableStateOf(true) }
    var inflationRate by rememberSaveable { mutableStateOf("") }
    var stepUpEnabled by rememberSaveable { mutableStateOf(true) }
    var stepUpValue by rememberSaveable { mutableStateOf("") }
    var isStepUpAmount by rememberSaveable { mutableStateOf(true) }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var sipResult by remember { mutableStateOf<AdvanceSIPResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        AdvanceSIPCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Initial Investment Input with Toggle
            AdvanceSIPInputFieldWithToggle(
                label = "Initial Investment",
                placeholder = "Ex: 1000",
                value = initialInvestment,
                onValueChange = { initialInvestment = it },
                enabled = initialInvestmentEnabled,
                onToggleChange = { initialInvestmentEnabled = it }
            )

            // Monthly Investment Input
            AdvanceSIPInputField(
                label = "Monthly Investment",
                placeholder = "Ex: 1000",
                value = monthlyInvestment,
                onValueChange = { monthlyInvestment = it }
            )

            // Expected Return Rate Input
            AdvanceSIPInputField(
                label = "Exp. Return Rate (%)",
                placeholder = "Ex: 12",
                value = expReturnRate,
                onValueChange = { expReturnRate = it }
            )

            // Period Input with Radio Buttons
            Column {
                AdvanceSIPInputField(
                    label = "Period (Years)",
                    placeholder = "Ex: 6",
                    value = period,
                    onValueChange = { period = it }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    AdvanceSIPRadioButton(
                        label = "Years",
                        selected = isPeriodYears,
                        onClick = { isPeriodYears = true }
                    )
                    AdvanceSIPRadioButton(
                        label = "Months",
                        selected = !isPeriodYears,
                        onClick = { isPeriodYears = false }
                    )
                }
            }

            // Inflation Rate Input with Toggle
            AdvanceSIPInputFieldWithToggle(
                label = "Inflation Rate (%)",
                placeholder = "Ex: 6",
                value = inflationRate,
                onValueChange = { inflationRate = it },
                enabled = inflationRateEnabled,
                onToggleChange = { inflationRateEnabled = it }
            )

            // Step Up Input with Toggle and Radio Buttons
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step Up",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = stepUpEnabled,
                        onCheckedChange = { stepUpEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFF424242),
                            uncheckedThumbColor = Color(0xFF9E9E9E),
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    OutlinedTextField(
                        value = stepUpValue,
                        onValueChange = { stepUpValue = it },
                        placeholder = {
                            Text(
                                text = "Ex: 6",
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
                        singleLine = true,
                        enabled = stepUpEnabled
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    AdvanceSIPRadioButton(
                        label = "AMOUNT",
                        selected = isStepUpAmount,
                        onClick = { isStepUpAmount = true }
                    )
                    AdvanceSIPRadioButton(
                        label = "PCT%",
                        selected = !isStepUpAmount,
                        onClick = { isStepUpAmount = false }
                    )
                }
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
                        errorMessage = null
                        val result = calculateAdvanceSIP(
                            initialInvestmentEnabled = initialInvestmentEnabled,
                            initialInvestment = initialInvestment,
                            monthlyInvestment = monthlyInvestment,
                            expReturnRate = expReturnRate,
                            period = period,
                            isPeriodYears = isPeriodYears,
                            stepUpEnabled = stepUpEnabled,
                            stepUpValue = stepUpValue,
                            isStepUpAmount = isStepUpAmount
                        )
                        if (result != null) {
                            sipResult = result
                            showResults = true
                            errorMessage = null
                        } else {
                            showResults = false
                            sipResult = null
                            errorMessage = when {
                                monthlyInvestment.isBlank() || monthlyInvestment.toDoubleOrNull() == null || monthlyInvestment.toDoubleOrNull()!! <= 0 ->
                                    "Please enter a valid monthly investment"
                                expReturnRate.isBlank() || expReturnRate.toDoubleOrNull() == null ->
                                    "Please enter a valid expected return rate"
                                period.isBlank() || period.toDoubleOrNull() == null || period.toDoubleOrNull()!! <= 0 ->
                                    "Please enter a valid period"
                                initialInvestmentEnabled && (initialInvestment.isBlank() || initialInvestment.toDoubleOrNull() == null || initialInvestment.toDoubleOrNull()!! < 0) ->
                                    "Please enter a valid initial investment"
                                stepUpEnabled && (stepUpValue.isBlank() || stepUpValue.toDoubleOrNull() == null || stepUpValue.toDoubleOrNull()!! < 0) ->
                                    "Please enter a valid step-up value"
                                else -> "Please check all input values"
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2079EC)
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
                        initialInvestment = ""
                        monthlyInvestment = ""
                        expReturnRate = ""
                        period = ""
                        inflationRate = ""
                        stepUpValue = ""
                        showResults = false
                        sipResult = null
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

            // Results Section
            AnimatedVisibility(
                visible = showResults && sipResult != null,
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
                                AdvanceSIPResultRow(
                                    label = "Total Investment",
                                    value = formatCurrencyWithDecimal(result.totalInvestment)
                                )
                                AdvanceSIPResultRow(
                                    label = "Estimated Returns",
                                    value = formatCurrencyWithDecimal(result.estimatedReturns)
                                )
                                AdvanceSIPResultRow(
                                    label = "Total Value",
                                    value = formatCurrencyWithDecimal(result.totalValue)
                                )
                            }
                        }
                        
                        // Pie Chart
                        AdvanceSIPDonutChart(
                            totalInvestment = result.totalInvestment,
                            estimatedReturns = result.estimatedReturns
                        )
                        
                        // Legend with percentages
                        val totalValue = result.totalInvestment + result.estimatedReturns
                        val investmentPercent = if (totalValue > 0) ((result.totalInvestment / totalValue) * 100).toInt() else 0
                        val returnsPercent = if (totalValue > 0) ((result.estimatedReturns / totalValue) * 100).toInt() else 0
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AdvanceSIPLegendItem(
                                color = Color(0xFF3F6EE4),
                                label = "$investmentPercent Total Investment"
                            )
                            Spacer(modifier = Modifier.width(24.dp))
                            AdvanceSIPLegendItem(
                                color = Color(0xFF00AF52),
                                label = "$returnsPercent Estimated Returns"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdvanceSIPCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "Advance SIP Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AdvanceSIPInputField(
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
fun AdvanceSIPInputFieldWithToggle(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    onToggleChange: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = enabled,
                onCheckedChange = onToggleChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = Color(0xFF424242),
                    uncheckedThumbColor = Color(0xFF9E9E9E),
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
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
            singleLine = true,
            enabled = enabled
        )
    }
}

@Composable
fun AdvanceSIPRadioButton(
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
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AdvanceSIPResultRow(
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
fun AdvanceSIPDonutChart(
    totalInvestment: Double,
    estimatedReturns: Double
) {
    val totalValue = totalInvestment + estimatedReturns
    val investmentPercentage = if (totalValue > 0) (totalInvestment / totalValue * 100).toFloat() else 0f
    val returnsPercentage = if (totalValue > 0) (estimatedReturns / totalValue * 100).toFloat() else 0f
    
    // Round percentages for display
    val investmentPercentRounded = investmentPercentage.toInt()
    val returnsPercentRounded = returnsPercentage.toInt()

    AndroidView(
        factory = { context ->
            MPAndroidPieChart(context).apply {
                setUsePercentValues(false)
                description.isEnabled = false
                setExtraOffsets(5f, 10f, 5f, 5f)
                dragDecelerationFrictionCoef = 0.95f
                isRotationEnabled = false
                isHighlightPerTapEnabled = false
                setDrawEntryLabels(true)
                setEntryLabelColor(android.graphics.Color.WHITE)
                setEntryLabelTextSize(14f)
                
                val entries = listOf(
                    PieEntry(investmentPercentage, "$investmentPercentRounded Total Investment"),
                    PieEntry(returnsPercentage, "$returnsPercentRounded Estimated Returns")
                )
                
                val dataSet = PieDataSet(entries, "").apply {
                    colors = listOf(
                        android.graphics.Color.parseColor("#3F6EE4"), // Blue for Total Investment
                        android.graphics.Color.parseColor("#00AF52")  // Green for Estimated Returns
                    )
                    setDrawValues(false)
                    valueTextColor = android.graphics.Color.BLACK
                    valueTextSize = 14f
                }
                
                data = PieData(dataSet)
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                setHoleRadius(58f)
                setTransparentCircleRadius(61f)
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 16.dp)
    )
}

@Composable
fun AdvanceSIPLegendItem(
    color: Color,
    label: String
) {
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
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

fun calculateAdvanceSIP(
    initialInvestmentEnabled: Boolean,
    initialInvestment: String,
    monthlyInvestment: String,
    expReturnRate: String,
    period: String,
    isPeriodYears: Boolean,
    stepUpEnabled: Boolean,
    stepUpValue: String,
    isStepUpAmount: Boolean
): AdvanceSIPResult? {
    return try {
        val monthly = monthlyInvestment.toDoubleOrNull() ?: return null
        val rate = expReturnRate.toDoubleOrNull() ?: return null
        var periodYears = period.toDoubleOrNull() ?: return null
        
        if (monthly <= 0 || rate < 0 || periodYears <= 0) return null
        
        // Convert period to years if entered in months
        if (!isPeriodYears) {
            periodYears = periodYears / 12.0
        }
        
        val years = periodYears.toInt()
        if (years <= 0) return null
        
        val initial = if (initialInvestmentEnabled) {
            initialInvestment.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }
        
        if (initialInvestmentEnabled && initial < 0) return null
        
        // Calculate step-up value (as amount or percentage)
        var stepUpAmount = 0.0
        var stepUpPercentage = 0.0
        var isStepUpByAmount = false
        if (stepUpEnabled) {
            val stepUpVal = stepUpValue.toDoubleOrNull() ?: return null
            if (stepUpVal < 0) return null
            
            if (isStepUpAmount) {
                // Step-up is entered as fixed amount (e.g., 6 rupees per year)
                stepUpAmount = stepUpVal
                isStepUpByAmount = true
            } else {
                // Step-up is entered as percentage
                stepUpPercentage = stepUpVal
                isStepUpByAmount = false
            }
        }
        
        // Calculate total future value based on scenario
        val totalFutureValue = when {
            // Scenario 1: Initial Investment + Regular SIP (No Step-Up)
            initialInvestmentEnabled && !stepUpEnabled -> {
                calculateLumpsum(initial, rate, years) + 
                calculateMaturityAmount(monthly, rate, years)
            }
            // Scenario 2: Step-Up SIP Only (No Initial Investment)
            stepUpEnabled && !initialInvestmentEnabled -> {
                if (isStepUpByAmount) {
                    calculateStepUpSIPByAmount(monthly, rate, stepUpAmount, years)
                } else {
                    calculateStepUpSIP(monthly, rate, stepUpPercentage, years)
                }
            }
            // Scenario 3: Initial Investment + Step-Up SIP
            stepUpEnabled && initialInvestmentEnabled -> {
                val stepUpFV = if (isStepUpByAmount) {
                    calculateStepUpSIPByAmount(monthly, rate, stepUpAmount, years)
                } else {
                    calculateStepUpSIP(monthly, rate, stepUpPercentage, years)
                }
                calculateLumpsum(initial, rate, years) + stepUpFV
            }
            // Default: Regular SIP only
            else -> {
                calculateMaturityAmount(monthly, rate, years)
            }
        }
        
        // Calculate total investment
        val totalInvested = if (stepUpEnabled) {
            val sipInvested = if (isStepUpByAmount) {
                calculateTotalInvestedValueForAdvancedSIPByAmount(monthly, stepUpAmount, years)
            } else {
                calculateTotalInvestedValueForAdvancedSIP(monthly, stepUpPercentage, years)
            }
            initial + sipInvested
        } else {
            initial + (monthly * years * 12)
        }
        
        // Calculate estimated returns
        val estimatedReturns = totalFutureValue - totalInvested
        
        AdvanceSIPResult(
            totalInvestment = totalInvested,
            estimatedReturns = estimatedReturns,
            totalValue = totalFutureValue
        )
    } catch (e: Exception) {
        null
    }
}

fun calculateLumpsum(principal: Double, annualRate: Double, years: Int): Double {
    if (principal <= 0 || years <= 0) return 0.0
    val annualRateDecimal = annualRate / 100.0
    return principal * java.lang.Math.pow(1 + annualRateDecimal, years.toDouble())
}

fun calculateMaturityAmount(monthlyInvestment: Double, annualRate: Double, years: Int): Double {
    if (monthlyInvestment <= 0 || years <= 0) return 0.0
    val monthlyRate = annualRate / (12 * 100)
    val totalMonths = years * 12
    
    return if (monthlyRate > 0) {
        val growthFactor = java.lang.Math.pow(1 + monthlyRate, totalMonths.toDouble())
        monthlyInvestment * ((growthFactor - 1) / monthlyRate) * (1 + monthlyRate)
    } else {
        monthlyInvestment * totalMonths
    }
}

fun calculateStepUpSIP(sipAmount: Double, annualRate: Double, stepUpRate: Double, years: Int): Double {
    if (sipAmount <= 0 || years <= 0) return 0.0
    
    var totalFutureValue = 0.0
    var currentSIP = sipAmount
    
    for (year in 1..years) {
        val remainingYears = years - year + 1
        val fv = calculateMaturityAmount(currentSIP, annualRate, remainingYears)
        totalFutureValue += fv
        
        // Increase SIP for next year
        if (stepUpRate > 0) {
            currentSIP *= (1 + stepUpRate / 100.0)
        }
    }
    
    return totalFutureValue
}

fun calculateTotalInvestedValueForAdvancedSIP(sipAmount: Double, stepUpRate: Double, years: Int): Double {
    if (sipAmount <= 0 || years <= 0) return 0.0
    
    var totalInvested = 0.0
    var currentSIP = sipAmount
    
    for (year in 1..years) {
        totalInvested += currentSIP * 12
        if (stepUpRate > 0) {
            currentSIP *= (1 + stepUpRate / 100.0)
        }
    }
    
    return totalInvested
}

fun calculateStepUpSIPByAmount(sipAmount: Double, annualRate: Double, stepUpAmount: Double, years: Int): Double {
    if (sipAmount <= 0 || years <= 0) return 0.0
    
    var totalFutureValue = 0.0
    var currentSIP = sipAmount
    
    for (year in 1..years) {
        val remainingYears = years - year + 1
        val fv = calculateMaturityAmount(currentSIP, annualRate, remainingYears)
        totalFutureValue += fv
        
        // Increase SIP by fixed amount for next year
        currentSIP += stepUpAmount
    }
    
    return totalFutureValue
}

fun calculateTotalInvestedValueForAdvancedSIPByAmount(sipAmount: Double, stepUpAmount: Double, years: Int): Double {
    if (sipAmount <= 0 || years <= 0) return 0.0
    
    var totalInvested = 0.0
    var currentSIP = sipAmount
    
    for (year in 1..years) {
        totalInvested += currentSIP * 12
        // Increase SIP by fixed amount for next year
        currentSIP += stepUpAmount
    }
    
    return totalInvested
}

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


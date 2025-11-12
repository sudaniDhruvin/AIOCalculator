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
import androidx.compose.material.icons.filled.ArrowDropDown
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

data class FDResult(
    val depositAmount: Double,
    val totalInterest: Double,
    val maturityAmount: Double
)

@Composable
fun FDCalculatorScreen(
    onBackClick: () -> Unit
) {
    // State variables
    var depositAmount by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }
    var years by rememberSaveable { mutableStateOf("") }
    var months by rememberSaveable { mutableStateOf("") }
    var days by rememberSaveable { mutableStateOf("") }
    var depositType by rememberSaveable { mutableStateOf("Reinvestment/Cumulative") }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var fdResult by rememberSaveable { mutableStateOf<FDResult?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        FDCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Deposit Amount Input
            FDInputField(
                label = "Deposit Amount",
                placeholder = "Ex: 10,00,000",
                value = depositAmount,
                onValueChange = { depositAmount = it }
            )

            // Interest % Input
            FDInputField(
                label = "Interest %",
                placeholder = "Ex: 6.5%",
                value = interestRate,
                onValueChange = { interestRate = it }
            )

            // Period Inputs in One Row
            Column {
                Text(
                    text = "Period",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Years Input
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Years",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = years,
                            onValueChange = { years = it },
                            placeholder = {
                                Text(
                                    text = "Ex: 1",
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

                    // Months Input
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Months",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = months,
                            onValueChange = { newValue ->
                                val numValue = newValue.toDoubleOrNull()
                                if (newValue.isEmpty() || (numValue != null && numValue >= 0 && numValue <= 12)) {
                                    months = newValue
                                }
                            },
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
                                focusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }

                    // Days Input
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Days",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = days,
                            onValueChange = { newValue ->
                                val numValue = newValue.toDoubleOrNull()
                                if (newValue.isEmpty() || (numValue != null && numValue >= 0 && numValue <= 31)) {
                                    days = newValue
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Ex: 30",
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
            }

            // Deposit Type Dropdown
            FDDepositTypeDropdown(
                label = "Deposit Type",
                value = depositType,
                onValueChange = { depositType = it }
            )

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
                        // Validate inputs
                        val monthsValue = months.toDoubleOrNull()
                        val daysValue = days.toDoubleOrNull()
                        
                        when {
                            depositAmount.isBlank() || depositAmount.toDoubleOrNull() == null || depositAmount.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid deposit amount"
                                showResults = false
                                fdResult = null
                            }
                            interestRate.isBlank() || interestRate.toDoubleOrNull() == null || interestRate.toDoubleOrNull()!! <= 0 -> {
                                errorMessage = "Please enter a valid interest rate"
                                showResults = false
                                fdResult = null
                            }
                            years.isBlank() && months.isBlank() && days.isBlank() -> {
                                errorMessage = "Please enter at least one period value"
                                showResults = false
                                fdResult = null
                            }
                            monthsValue != null && monthsValue > 12 -> {
                                errorMessage = "Months cannot exceed 12"
                                showResults = false
                                fdResult = null
                            }
                            daysValue != null && daysValue > 31 -> {
                                errorMessage = "Days cannot exceed 31"
                                showResults = false
                                fdResult = null
                            }
                            else -> {
                                val result = calculateFD(
                                    depositAmount,
                                    interestRate,
                                    years,
                                    months,
                                    days,
                                    depositType
                                )
                                if (result != null) {
                                    fdResult = result
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Please check all input values"
                                    showResults = false
                                    fdResult = null
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
                        depositAmount = ""
                        interestRate = ""
                        years = ""
                        months = ""
                        days = ""
                        depositType = "Reinvestment/Cumulative"
                        showResults = false
                        fdResult = null
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
                visible = showResults && fdResult != null,
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
                fdResult?.let { result ->
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
                                // Total Interest
                                FDResultRow(
                                    "Total Interest",
                                    formatCurrencyWithDecimal(result.totalInterest)
                                )
                                
                                // Maturity Amount
                                FDResultRow(
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
                                FDDonutChart(
                                    depositAmount = result.depositAmount,
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
                                    // Deposit Amount Legend
                                    FDLegendItem(
                                        label = "Deposit Amount",
                                        color = Color(0xFF3F6EE4) // Blue - matches pie chart
                                    )
                                    
                                    Spacer(modifier = Modifier.width(24.dp))
                                    
                                    // Total Interest Legend
                                    FDLegendItem(
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
fun FDCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "FD Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FDInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
fun FDDepositTypeDropdown(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val depositTypeOptions = listOf(
        "Reinvestment/Cumulative",
        "Quarterly Payout",
        "Monthly Payout",
        "Short Term"
    )

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    disabledContainerColor = Color(0xFFF5F5F5),
                    disabledBorderColor = Color.Transparent,
                    disabledTextColor = Color.Black
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.Gray
                    )
                }
            )
        }
        
        AnimatedVisibility(
            visible = expanded,
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    depositTypeOptions.forEachIndexed { index, option ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(option)
                                    expanded = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        if (index < depositTypeOptions.size - 1) {
                            Divider(
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FDResultRow(
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
fun FDDonutChart(
    depositAmount: Double,
    totalInterest: Double,
    modifier: Modifier = Modifier
) {
    val total = depositAmount + totalInterest
    val depositPercentage = if (total > 0) (depositAmount / total * 100).toFloat() else 0f
    val interestPercentage = if (total > 0) (totalInterest / total * 100).toFloat() else 0f

    AndroidView(
        factory = { ctx ->
            try {
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
                entries.add(PieEntry(depositPercentage, "Deposit Amount"))
                entries.add(PieEntry(interestPercentage, "Total Interest"))

                val dataSet = PieDataSet(entries, "").apply {
                    colors = listOf(
                        android.graphics.Color.parseColor("#3F6EE4"), // Blue for Deposit
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
                    entries.add(PieEntry(depositPercentage, "Deposit Amount"))
                    entries.add(PieEntry(interestPercentage, "Total Interest"))

                    val dataSet = PieDataSet(entries, "").apply {
                        colors = listOf(
                            android.graphics.Color.parseColor("#3F6EE4"), // Blue for Deposit
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
                }
            } catch (e: Exception) {
                // Ignore exceptions during chart updates to prevent crashes
            }
        },
        modifier = modifier
    )
}

@Composable
fun FDLegendItem(
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

fun calculateFD(
    depositAmount: String,
    interestRate: String,
    years: String,
    months: String,
    days: String,
    depositType: String
): FDResult? {
    return try {
        val principal = depositAmount.toDoubleOrNull() ?: return null
        val rate = interestRate.toDoubleOrNull() ?: return null
        val yearsValue = years.toDoubleOrNull() ?: 0.0
        val monthsValue = months.toDoubleOrNull() ?: 0.0
        val daysValue = days.toDoubleOrNull() ?: 0.0
        
        if (principal <= 0 || rate <= 0) return null
        
        // Convert all to years: years + months/12 + days/365
        val totalYears = yearsValue + (monthsValue / 12.0) + (daysValue / 365.0)
        
        if (totalYears <= 0) return null
        
        val r = rate / 100.0
        
        // Calculate based on deposit type
        val maturityAmount = when (depositType) {
            "Reinvestment/Cumulative" -> {
                // Compound interest: A = P * (1 + r)^n
                principal * (1 + r).pow(totalYears)
            }
            "Quarterly Payout" -> {
                // Quarterly compounding: A = P * (1 + r/4)^(4n)
                principal * (1 + r / 4).pow(4 * totalYears)
            }
            "Monthly Payout" -> {
                // Monthly compounding: A = P * (1 + r/12)^(12n)
                principal * (1 + r / 12).pow(12 * totalYears)
            }
            "Short Term" -> {
                // Simple interest for short term: A = P * (1 + r * n)
                principal * (1 + r * totalYears)
            }
            else -> {
                // Default to compound interest
                principal * (1 + r).pow(totalYears)
            }
        }
        
        val totalInterest = maturityAmount - principal
        
        FDResult(
            depositAmount = principal,
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


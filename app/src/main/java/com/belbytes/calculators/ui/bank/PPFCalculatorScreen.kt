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

data class PPFResult(
    val totalInvestment: Double,
    val totalInterest: Double,
    val maturityAmount: Double
)

@Composable
fun PPFCalculatorScreen(
    onBackClick: () -> Unit
) {
    // State variables
    var frequency by rememberSaveable { mutableStateOf("Yearly") }
    var investment by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }
    var period by rememberSaveable { mutableStateOf("15") }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var ppfResult by rememberSaveable { mutableStateOf<PPFResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        PPFCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Frequency Dropdown
            PPFDropdownField(
                label = "Frequency",
                value = frequency,
                onValueChange = { frequency = it }
            )

            // Investment Input
            PPFInputField(
                label = "Investment",
                placeholder = "Ex: 10,000",
                value = investment,
                onValueChange = { investment = it }
            )

            // Interest % Input
            PPFInputField(
                label = "Interest %",
                placeholder = "Ex: 7.1%",
                value = interestRate,
                onValueChange = { interestRate = it }
            )

            // Period (Years) Input
            PPFInputField(
                label = "Period (Years)",
                placeholder = "Ex: 15",
                value = period,
                onValueChange = { period = it }
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
                        val result = calculatePPF(
                            frequency,
                            investment,
                            interestRate,
                            period
                        )
                        if (result != null) {
                            ppfResult = result
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
                        frequency = "Yearly"
                        investment = ""
                        interestRate = ""
                        period = "15"
                        showResults = false
                        ppfResult = null
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
                visible = showResults && ppfResult != null,
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
                ppfResult?.let { result ->
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
                                PPFResultRow(
                                    "Total Investment",
                                    formatCurrencyWithDecimal(result.totalInvestment)
                                )
                                
                                // Total Interest
                                PPFResultRow(
                                    "Total Interest",
                                    formatCurrencyWithDecimal(result.totalInterest)
                                )
                                
                                // Maturity Amount
                                PPFResultRow(
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
                                PPFDonutChart(
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
                                    PPFLegendItem(
                                        label = "Total Investment",
                                        color = Color(0xFF3F6EE4) // Blue - matches pie chart
                                    )
                                    
                                    Spacer(modifier = Modifier.width(24.dp))
                                    
                                    // Total Interest Legend
                                    PPFLegendItem(
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
fun PPFCalculatorHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color(0xFF4257B2))
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
            text = "PPF Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PPFDropdownField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val frequencyOptions = listOf("Yearly")

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
                    frequencyOptions.forEachIndexed { index, option ->
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
                        if (index < frequencyOptions.size - 1) {
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
fun PPFInputField(
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
fun PPFResultRow(
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

fun calculatePPF(
    frequency: String,
    investment: String,
    interestRate: String,
    period: String
): PPFResult? {
    return try {
        val investmentValue = investment.toDoubleOrNull() ?: return null
        val rate = interestRate.toDoubleOrNull() ?: return null
        val years = period.toDoubleOrNull() ?: return null
        
        if (years <= 0 || rate <= 0 || investmentValue <= 0) return null
        
        val r = rate / 100.0
        val n = years.toInt()
        
        // PPF Formula: Maturity Amount = P * [((1 + r)^n - 1) / r] * (1 + r)
        // This assumes investment is made at the beginning of each year
        val maturityAmount = if (r > 0) {
            investmentValue * (((1 + r).pow(n) - 1) / r) * (1 + r)
        } else {
            investmentValue * n
        }
        
        val totalInvestment = investmentValue * n
        val totalInterest = maturityAmount - totalInvestment
        
        PPFResult(
            totalInvestment = totalInvestment,
            totalInterest = totalInterest,
            maturityAmount = maturityAmount
        )
    } catch (e: Exception) {
        null
    }
}

@Composable
fun PPFDonutChart(
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
                        android.graphics.Color.parseColor("#3F6EE4"), // Blue for Principal
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
                    android.graphics.Color.parseColor("#3F6EE4"), // Blue for Principal
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
fun PPFLegendItem(
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

private fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


package com.example.aiocalculator.ui.emi

import android.annotation.SuppressLint
import android.os.Parcelable
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart as MPAndroidPieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlin.math.ceil
import kotlin.math.ln
import kotlinx.parcelize.Parcelize

@Composable
fun EMICalculatorScreen(
    onBackClick: () -> Unit,
    onCalculateClick: () -> Unit,
    onViewDetails: (EMIResult, Double, Double) -> Unit = { _, _, _ -> }
) {
    var emiType by remember { mutableStateOf("EMI") }
    var amount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var periodType by remember { mutableStateOf(PeriodType.YEARS) }
    var period by remember { mutableStateOf("") }
    var emi by remember { mutableStateOf("") }
    var processingFee by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }
    var emiResult by remember { mutableStateOf<EMIResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        EMICalculatorHeader(
            onBackClick = onBackClick
        )

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // EMI Dropdown
            EMIDropdownField(
                label = "EMI",
                value = emiType,
                onValueChange = { emiType = it }
            )

            // Amount Input
            EMIInputField(
                label = "Amount",
                placeholder = "Ex: 500,000",
                value = amount,
                onValueChange = { amount = it }
            )

            // Interest Rate Input
            EMIInputField(
                label = "Interest Rate (%)",
                placeholder = "Ex: 5%",
                value = interestRate,
                onValueChange = { interestRate = it }
            )

            // Conditional Fields based on EMI Type
            if (emiType == "EMI") {
                // Period Type Radio Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    PeriodTypeRadioButton(
                        label = "Years",
                        selected = periodType == PeriodType.YEARS,
                        onClick = { periodType = PeriodType.YEARS }
                    )
                    PeriodTypeRadioButton(
                        label = "Month",
                        selected = periodType == PeriodType.MONTH,
                        onClick = { periodType = PeriodType.MONTH }
                    )
                }

                // Period Input
                EMIInputField(
                    label = "Period",
                    placeholder = "Ex: 5",
                    value = period,
                    onValueChange = { period = it }
                )
            } else if (emiType == "Loan Tenure") {
                // EMI Input
                EMIInputField(
                    label = "EMI",
                    placeholder = "Ex: 10,000",
                    value = emi,
                    onValueChange = { emi = it }
                )
            }

            // Processing Fee Input
            EMIInputField(
                label = "Processing Fee (%)",
                placeholder = "Ex: 2%",
                value = processingFee,
                onValueChange = { processingFee = it }
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
                        val result = calculateEMI(
                            emiType = emiType,
                            amount = amount,
                            interestRate = interestRate,
                            period = period,
                            periodType = periodType,
                            emi = emi,
                            processingFee = processingFee
                        )
                        if (result != null) {
                            emiResult = result
                            showResults = true
                        }
                        onCalculateClick()
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
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Reset Button
                Button(
                    onClick = {
                        amount = ""
                        interestRate = ""
                        period = ""
                        emi = ""
                        processingFee = ""
                        periodType = PeriodType.YEARS
                        showResults = false
                        emiResult = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEBEBEB)
                    ),
                    shape = RoundedCornerShape(12.dp),
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

            // Results Section
            AnimatedVisibility(
                visible = showResults && emiResult != null,
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
                emiResult?.let { result ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Spacer after button
                        Spacer(modifier = Modifier.height(30.dp))

                        // Horizontal Divider
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        // EMI Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF7F7F7)
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ResultRow("Monthly EMI", formatCurrency(result.monthlyEMI))
                                ResultRow(
                                    "Period (months)",
                                    "${result.periodMonths / 12} years, ${result.periodMonths % 12} months (${result.periodMonths} months total)"
                                )
                                ResultRow("Total Interest", formatCurrency(result.totalInterest))
                                ResultRow("Processing Fees", formatCurrency(result.processingFees))
                                ResultRow("Total Payment", formatCurrency(result.totalPayment))
                            }
                        }

                        // Chart Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF7F7F7)
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Donut Chart with built-in legend
                                DonutChart(
                                    principal = result.principal,
                                    interest = result.totalInterest,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                        }
                        // View Details Button
                        Button(
                            onClick = {
                                emiResult?.let { result ->
                                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                                    val interestRateValue = interestRate.toDoubleOrNull() ?: 0.0
                                    onViewDetails(result, amountValue, interestRateValue)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 25.dp, start = 28.dp, end = 28.dp, bottom = 35.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2079EC)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "View Details",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "View Details",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun calculateEMI(
    emiType: String,
    amount: String,
    interestRate: String,
    period: String,
    periodType: PeriodType,
    emi: String,
    processingFee: String
): EMIResult? {
    try {
        val principal = amount.toDoubleOrNull() ?: return null
        val rate = interestRate.toDoubleOrNull() ?: return null
        val processingFeePercent = processingFee.toDoubleOrNull() ?: 0.0

        if (emiType == "EMI") {
            val periodValue = period.toDoubleOrNull() ?: return null
            val months = if (periodType == PeriodType.YEARS) {
                (periodValue * 12).toInt()
            } else {
                periodValue.toInt()
            }

            val monthlyRate = rate / (12 * 100)
            val monthlyEMI = if (monthlyRate > 0) {
                val rateFactor = java.lang.Math.pow(1 + monthlyRate, months.toDouble())
                principal * monthlyRate * rateFactor / (rateFactor - 1)
            } else {
                principal / months
            }

            val totalPayment = monthlyEMI * months
            val totalInterest = totalPayment - principal
            val processingFees = principal * (processingFeePercent / 100)

            return EMIResult(
                monthlyEMI = monthlyEMI,
                periodMonths = months,
                totalInterest = totalInterest,
                processingFees = processingFees,
                totalPayment = totalPayment + processingFees,
                principal = principal
            )
        } else if (emiType == "Loan Tenure") {
            val emiValue = emi.toDoubleOrNull() ?: return null
            val monthlyRate = rate / (12 * 100)

            val months = if (monthlyRate > 0) {
                ceil(
                    -ln(1 - (principal * monthlyRate) / emiValue) /
                            ln(1 + monthlyRate)
                ).toInt()
            } else {
                (principal / emiValue).toInt()
            }

            val totalPayment = emiValue * months
            val totalInterest = totalPayment - principal
            val processingFees = principal * (processingFeePercent / 100)

            return EMIResult(
                monthlyEMI = emiValue,
                periodMonths = months,
                totalInterest = totalInterest,
                processingFees = processingFees,
                totalPayment = totalPayment + processingFees,
                principal = principal
            )
        }
    } catch (e: Exception) {
        return null
    }
    return null
}

fun formatCurrency(amount: Double): String {
    return String.format("%,.0f", amount)
}

@Composable
fun ResultRow(label: String, value: String) {
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
fun DonutChart(
    principal: Double,
    interest: Double,
    modifier: Modifier = Modifier
) {
    PieChart(
        principal = principal,
        interest = interest,
        modifier = modifier
    )
}

@Composable
fun PieChart(
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
                // Configure chart appearance - Donut chart
                description.isEnabled = false
                setUsePercentValues(false) // Use actual percentage values
                setDrawEntryLabels(true)
                setEntryLabelTextSize(12f)
                setEntryLabelColor(android.graphics.Color.WHITE)
                setCenterText("")
                setDrawCenterText(false)
                setHoleRadius(50f) // Make it a donut chart (50% hole radius)
                setTransparentCircleRadius(0f)
                rotationAngle = -90f // Start from top
                setRotationEnabled(false)
                animateY(1000)
                
                // Configure legend
                legend.isEnabled = true
                legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.CENTER
                legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT
                legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
                legend.setDrawInside(false)
                legend.xEntrySpace = 10f
                legend.yEntrySpace = 12f
                legend.textSize = 14f
                legend.textColor = android.graphics.Color.BLACK
                legend.form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
                legend.formSize = 12f
                legend.formToTextSpace = 8f

                // Create data entries with labels
                val entries = mutableListOf<PieEntry>()
                entries.add(PieEntry(principalPercentage, "Principal"))
                entries.add(PieEntry(interestPercentage, "Interest"))
                
                // Create dataset
                val dataSet = PieDataSet(entries, "").apply {
                    colors = listOf(
                        android.graphics.Color.parseColor("#3F6EE4"), // Blue for Principal
                        android.graphics.Color.parseColor("#00AF52")  // Green for Interest
                    )
                    valueTextSize = 14f
                    valueTextColor = android.graphics.Color.WHITE
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return String.format("%.2f", value)
                        }
                    }
                    setDrawValues(true)
                    // Show values and labels inside segments
                    setYValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                    setXValuePosition(com.github.mikephil.charting.data.PieDataSet.ValuePosition.INSIDE_SLICE)
                    setSliceSpace(2f) // Add spacing between segments (exploded effect)
                }
                
                // Set data
                data = PieData(dataSet)
                invalidate()
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
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun EMICalculatorHeader(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF2196F3))
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
            text = "EMI Calculator",
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
fun EMIDropdownField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val emiOptions = listOf("EMI", "Loan Tenure")

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
                    emiOptions.forEachIndexed { index, option ->
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
                        if (index < emiOptions.size - 1) {
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
fun EMIInputField(
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
fun PeriodTypeRadioButton(
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
                selectedColor = Color(0xFF757575),
                unselectedColor = Color(0xFF757575)
            )
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

enum class PeriodType {
    YEARS,
    MONTH
}

@Parcelize
data class EMIResult(
    val monthlyEMI: Double,
    val periodMonths: Int,
    val totalInterest: Double,
    val processingFees: Double,
    val totalPayment: Double,
    val principal: Double
) : Parcelable


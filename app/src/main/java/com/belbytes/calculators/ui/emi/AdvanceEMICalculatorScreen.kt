package com.belbytes.calculators.ui.emi

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
import androidx.compose.material.icons.filled.ArrowForward
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
import kotlin.math.ceil
import kotlin.math.pow
import kotlinx.parcelize.Parcelize

@Composable
fun AdvanceEMICalculatorScreen(
    onBackClick: () -> Unit,
    onViewDetails: (AdvanceEMIResult, Double, Double, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    // State variables
    var amount by rememberSaveable { mutableStateOf("") }
    var interestRate by rememberSaveable { mutableStateOf("") }
    var interestType by rememberSaveable { mutableStateOf("Reducing") } // "Reducing" or "Flat"
    var period by rememberSaveable { mutableStateOf("") }
    var periodType by rememberSaveable { mutableStateOf("Years") } // "Years" or "Months"
    var processingFee by rememberSaveable { mutableStateOf("") }
    var processingFeeType by rememberSaveable { mutableStateOf("%") } // "%" or "₹"
    var gstOnInterest by rememberSaveable { mutableStateOf("") }
    var emiType by rememberSaveable { mutableStateOf("EMI In Arrears") } // "EMI In Arrears" or "EMI In Advance"
    var showResults by rememberSaveable { mutableStateOf(false) }
    var emiResult by remember { mutableStateOf<AdvanceEMIResult?>(null) }
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
            // Amount Input
            AdvanceEMIInputField(
                label = "Amount",
                placeholder = "Ex: 500,000",
                value = amount,
                onValueChange = { amount = it }
            )

            // Interest Rate Input with Radio Buttons
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Interest Rate (%)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AdvanceEMIRadioButton(
                            label = "Reducing",
                            selected = interestType == "Reducing",
                            onClick = { interestType = "Reducing" }
                        )
                        AdvanceEMIRadioButton(
                            label = "Flat",
                            selected = interestType == "Flat",
                            onClick = { interestType = "Flat" }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                AdvanceEMIInputField(
                    label = "",
                    placeholder = "Ex: 5%",
                    value = interestRate,
                    onValueChange = { interestRate = it }
                )
            }

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
                        color = Color(0xFF222222),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AdvanceEMIRadioButton(
                            label = "Years",
                            selected = periodType == "Years",
                            onClick = { periodType = "Years" }
                        )
                        AdvanceEMIRadioButton(
                            label = "Months",
                            selected = periodType == "Months",
                            onClick = { periodType = "Months" }
                        )
                    }
                }
                AdvanceEMIInputField(
                    label = "",
                    placeholder = "Ex: 10",
                    value = period,
                    onValueChange = { period = it }
                )
            }

            // Processing Fees Input with Radio Buttons
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Processing Fees",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222),
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AdvanceEMIRadioButton(
                            label = "%",
                            selected = processingFeeType == "%",
                            onClick = { processingFeeType = "%" }
                        )
                        AdvanceEMIRadioButton(
                            label = "₹",
                            selected = processingFeeType == "₹",
                            onClick = { processingFeeType = "₹" }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                AdvanceEMIInputField(
                    label = "",
                    placeholder = "Ex: 3",
                    value = processingFee,
                    onValueChange = { processingFee = it }
                )
            }

            // GST On Interest Input
            AdvanceEMIInputField(
                label = "GST On Interest",
                placeholder = "Ex: 5",
                value = gstOnInterest,
                onValueChange = { gstOnInterest = it }
            )

            // EMI Type Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AdvanceEMIRadioButton(
                    label = "EMI In Arrears",
                    selected = emiType == "EMI In Arrears",
                    onClick = { emiType = "EMI In Arrears" }
                )
                AdvanceEMIRadioButton(
                    label = "EMI In Advance",
                    selected = emiType == "EMI In Advance",
                    onClick = { emiType = "EMI In Advance" }
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
                            amount.isBlank() || (amount.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = "Please enter a valid loan amount"
                                showResults = false
                                emiResult = null
                            }
                            interestRate.isBlank() || (interestRate.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = "Please enter a valid interest rate"
                                showResults = false
                                emiResult = null
                            }
                            period.isBlank() || (period.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = "Please enter a valid period"
                                showResults = false
                                emiResult = null
                            }
                            else -> {
                                val result = calculateAdvanceEMI(
                                    amount = amount,
                                    interestRate = interestRate,
                                    interestType = interestType,
                                    period = period,
                                    periodType = periodType,
                                    processingFee = processingFee,
                                    processingFeeType = processingFeeType,
                                    gstOnInterest = gstOnInterest,
                                    emiType = emiType
                                )
                                if (result != null) {
                                    emiResult = result
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Please check all input values"
                                    showResults = false
                                    emiResult = null
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
                        interestType = "Reducing"
                        period = ""
                        periodType = "Years"
                        processingFee = ""
                        processingFeeType = "%"
                        gstOnInterest = ""
                        emiType = "EMI In Arrears"
                        showResults = false
                        errorMessage = null
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
                                AdvanceEMIResultRow("Monthly EMI", formatAdvanceCurrency(result.monthlyEMI))
                                AdvanceEMIResultRow("Period (months)", "${result.periodMonths} months")
                                AdvanceEMIResultRow("Total Interest", formatAdvanceCurrency(result.totalInterest))
                                AdvanceEMIResultRow("Processing Fees", formatAdvanceCurrency(result.processingFees))
                                AdvanceEMIResultRow("GST On interest", formatAdvanceCurrency(result.gstOnInterest))
                                AdvanceEMIResultRow("GST On Processing Fees", formatAdvanceCurrency(result.gstOnProcessingFees))
                                AdvanceEMIResultRow("Total Payment", formatAdvanceCurrency(result.totalPayment))
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
                                // Donut Chart
                                AdvanceEMIPieChart(
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
                                emiResult?.let { res ->
                                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                                    val interestRateValue = interestRate.toDoubleOrNull() ?: 0.0
                                    onViewDetails(res, amountValue, interestRateValue, interestType, emiType)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 25.dp, start = 28.dp, end = 28.dp, bottom = 35.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
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
                text = "Advance EMI Calculator",
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
fun AdvanceEMIHeader(onBackClick: () -> Unit) {
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
            text = "Advance EMI Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AdvanceEMIInputField(
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
                color = Color(0xFF222222),
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
fun AdvanceEMIRadioButton(
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
            color = Color(0xFF222222),
            modifier = Modifier.padding(start = 0.dp)
        )
    }
}

@Composable
fun AdvanceEMIResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AdvanceEMIPieChart(
    principal: Double,
    interest: Double,
    modifier: Modifier = Modifier
) {
    val total = principal + interest
    val principalPercentage = (principal / total * 100).toFloat()
    val interestPercentage = (interest / total * 100).toFloat()

    AndroidView(
        factory = { ctx ->
            try {
                MPAndroidPieChart(ctx).apply {
                description.isEnabled = false
                setUsePercentValues(false)
                setDrawEntryLabels(false) // Remove labels, show only percentages
                setCenterText("")
                setDrawCenterText(false)
                setHoleRadius(50f)
                setTransparentCircleRadius(0f)
                rotationAngle = -90f
                setRotationEnabled(false)
                
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

fun calculateAdvanceEMI(
    amount: String,
    interestRate: String,
    interestType: String,
    period: String,
    periodType: String,
    processingFee: String,
    processingFeeType: String,
    gstOnInterest: String,
    emiType: String
): AdvanceEMIResult? {
    try {
        val principal = amount.toDoubleOrNull() ?: return null
        val rate = interestRate.toDoubleOrNull() ?: return null
        val gstRate = gstOnInterest.toDoubleOrNull() ?: 0.0
        val periodValue = period.toDoubleOrNull() ?: return null

        // Step 1: Convert period to months
        val months = if (periodType == "Years") {
            (periodValue * 12).toInt()
        } else {
            periodValue.toInt()
        }

        // Step 2: Calculate processing fees
        val processingFees = if (processingFeeType == "%") {
            val feePercent = processingFee.toDoubleOrNull() ?: 0.0
            (principal * feePercent) / 100
        } else {
            processingFee.toDoubleOrNull() ?: 0.0
        }

        // Step 3: Calculate GST on Processing Fees (fixed at 18%)
        val gstOnProcessingFees = (processingFees * 18) / 100

        // Step 4: Calculate EMI based on interest type and payment mode
        val monthlyRate = rate / (12 * 100)
        val monthlyEMI: Double
        var totalAmount: Double
        val totalInterest: Double

        if (interestType == "Reducing") {
            // Reducing Balance Interest Calculation
            if (emiType == "EMI In Arrears") {
                // Arrears Mode: Payment at the end of the month
                if (monthlyRate > 0) {
                    val rateFactor = java.lang.Math.pow(1 + monthlyRate, months.toDouble())
                    monthlyEMI = (principal * monthlyRate * rateFactor) / (rateFactor - 1)
                } else {
                    monthlyEMI = principal / months
                }
                totalAmount = monthlyEMI * months
                totalInterest = totalAmount - principal
            } else {
                // Advance Mode: Payment at the beginning of the month
                // EMI = ((P - EMI) × r × (1 + r)^(n-1)) / ((1 + r)^(n-1) - 1)
                // This requires iterative calculation since EMI appears on both sides
                if (monthlyRate > 0) {
                    // Initial estimate for EMI (using arrears formula)
                    val rateFactorArrears = java.lang.Math.pow(1 + monthlyRate, months.toDouble())
                    var estimatedEMI = (principal * monthlyRate * rateFactorArrears) / (rateFactorArrears - 1)
                    
                    // Iterative calculation for advance mode
                    var previousEMI = 0.0
                    var iterations = 0
                    while (kotlin.math.abs(estimatedEMI - previousEMI) > 0.01 && iterations < 100) {
                        previousEMI = estimatedEMI
                        val adjustedPrincipal = principal - estimatedEMI
                        val rateFactorAdvance = java.lang.Math.pow(1 + monthlyRate, (months - 1).toDouble())
                        estimatedEMI = (adjustedPrincipal * monthlyRate * rateFactorAdvance) / (rateFactorAdvance - 1)
                        iterations++
                    }
                    monthlyEMI = estimatedEMI
                } else {
                    monthlyEMI = principal / months
                }
                totalAmount = monthlyEMI * months
                totalInterest = totalAmount - principal
            }
        } else {
            // Flat Interest Calculation
            // Step 4.1: Calculate Total Interest
            totalInterest = principal * (rate / 100) * (months / 12.0)
            
            // Step 4.2: Calculate Total Amount (including processing fees for EMI calculation)
            totalAmount = principal + totalInterest + processingFees
            
            // Step 4.3: Calculate EMI based on payment mode
            if (emiType == "EMI In Arrears") {
                // Arrears Mode: EMI = Total Amount / Total Months
                monthlyEMI = totalAmount / months
            } else {
                // Advance Mode: EMI = (Total Amount - EMI) / Total Months
                // Solving: EMI = (Total Amount - EMI) / months
                // EMI * months = Total Amount - EMI
                // EMI * months + EMI = Total Amount
                // EMI * (months + 1) = Total Amount
                // EMI = Total Amount / (months + 1)
                monthlyEMI = totalAmount / (months + 1)
            }
            
            // For flat interest, totalAmount is used for EMI calculation only
            // The actual total amount paid is EMI * months
            totalAmount = monthlyEMI * months
        }

        // Step 5: Calculate GST on Interest
        val gstOnInterestAmount = (totalInterest * gstRate) / 100

        // Step 6: Calculate Final Total Payment
        // According to document: Total Payment = Total Amount + Processing Fees + GST on Processing Fees + GST on Interest
        // However, for flat interest, totalAmount already includes processingFees in EMI calculation
        // So we need to handle it differently to avoid double counting
        val finalTotalPayment = if (interestType == "Flat") {
            // For flat interest: totalAmount = EMI * months (includes principal + interest + processing fees in EMI)
            // Final Total Payment = EMI * months + GST on Processing Fees + GST on Interest
            // Which equals: principal + interest + processing fees + GST on Processing Fees + GST on Interest
            totalAmount + gstOnProcessingFees + gstOnInterestAmount
        } else {
            // For reducing balance: totalAmount = EMI * months = principal + interest
            // Final Total Payment = principal + interest + processing fees + GST on Processing Fees + GST on Interest
            totalAmount + processingFees + gstOnProcessingFees + gstOnInterestAmount
        }

        return AdvanceEMIResult(
            monthlyEMI = monthlyEMI,
            periodMonths = months,
            totalInterest = totalInterest,
            processingFees = processingFees,
            gstOnInterest = gstOnInterestAmount,
            gstOnProcessingFees = gstOnProcessingFees,
            totalPayment = finalTotalPayment,
            principal = principal
        )
    } catch (e: Exception) {
        return null
    }
}

fun formatAdvanceCurrency(amount: Double): String {
    return String.format("%,.0f", amount)
}

@Parcelize
data class AdvanceEMIResult(
    val monthlyEMI: Double,
    val periodMonths: Int,
    val totalInterest: Double,
    val processingFees: Double,
    val gstOnInterest: Double,
    val gstOnProcessingFees: Double,
    val totalPayment: Double,
    val principal: Double
) : Parcelable


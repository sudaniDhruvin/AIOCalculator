package com.belbytes.calculators.ui.gst

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.belbytes.calculators.R
import com.belbytes.calculators.utils.formatCurrencyWithDecimal

data class GSTResult(
    val initialAmount: Double,
    val gstAmount: Double,
    val totalAmount: Double,
    val cgstAmount: Double,
    val sgstAmount: Double,
    val cgstRate: Double,
    val sgstRate: Double
)

@Composable
fun GSTCalculatorScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val addGstLabel = context.getString(R.string.add_gst)
    
    // State variables
    var initialAmount by rememberSaveable { mutableStateOf("") }
    var gstRate by rememberSaveable { mutableStateOf("") }
    var gstOperation by rememberSaveable { mutableStateOf(addGstLabel) }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var gstResult by remember { mutableStateOf<GSTResult?>(null) }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        GSTCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Initial Amount Input
            GSTInputField(
                label = context.getString(R.string.amount),
                placeholder = context.getString(R.string.placeholder_amount),
                value = initialAmount,
                onValueChange = { initialAmount = it }
            )

            // Rate of GST Input
            GSTInputField(
                label = context.getString(R.string.gst_rate),
                placeholder = context.getString(R.string.placeholder_rate),
                value = gstRate,
                onValueChange = { gstRate = it }
            )

            // GST Operation Radio Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                GSTRadioButton(
                    label = context.getString(R.string.add_gst),
                    selected = gstOperation == context.getString(R.string.add_gst),
                    onClick = { gstOperation = context.getString(R.string.add_gst) }
                )
                GSTRadioButton(
                    label = context.getString(R.string.remove_gst),
                    selected = gstOperation == context.getString(R.string.remove_gst),
                    onClick = { gstOperation = context.getString(R.string.remove_gst) }
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
                            initialAmount.isBlank() || (initialAmount.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = context.getString(R.string.error_invalid_amount)
                                showResults = false
                                gstResult = null
                            }
                            gstRate.isBlank() || (gstRate.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = context.getString(R.string.error_invalid_gst_rate)
                                showResults = false
                                gstResult = null
                            }
                            else -> {
                                val result = calculateGST(initialAmount, gstRate, gstOperation, addGstLabel)
                                if (result != null) {
                                    gstResult = result
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    errorMessage = context.getString(R.string.error_check_inputs)
                                    showResults = false
                                    gstResult = null
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
                        text = context.getString(R.string.calculate),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Reset Button
                Button(
                    onClick = {
                        initialAmount = ""
                        gstRate = ""
                        gstOperation = addGstLabel
                        showResults = false
                        gstResult = null
                        errorMessage = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = context.getString(R.string.reset),
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

            // Results Section Card
            AnimatedVisibility(
                visible = showResults && gstResult != null,
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
                gstResult?.let { result ->
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
                            // Initial Amount
                            GSTResultRow(context.getString(R.string.initial_amount), formatCurrencyWithDecimal(context, result.initialAmount))
                            
                            // GST Amount
                            GSTResultRow(context.getString(R.string.gst_amount), formatCurrencyWithDecimal(context, result.gstAmount))
                            
                            // Total Amount
                            GSTResultRow(context.getString(R.string.total_amount), formatCurrencyWithDecimal(context, result.totalAmount))
                            
                            // CGST and SGST Breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // CGST Column
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "${context.getString(R.string.cgst)} : ${String.format("%.1f", result.cgstRate)}% =",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = formatCurrencyWithDecimal(context, result.cgstAmount),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                                
                                // SGST Column
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "${context.getString(R.string.sgst)} : ${String.format("%.1f", result.sgstRate)}% =",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = formatCurrencyWithDecimal(context, result.sgstAmount),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
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
fun GSTCalculatorHeader(onBackClick: () -> Unit) {
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
            text = context.getString(R.string.gst_calculator),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GSTInputField(
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
fun GSTRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
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
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun GSTResultRow(
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

fun calculateGST(
    initialAmount: String,
    gstRate: String,
    gstOperation: String,
    addGstLabel: String
): GSTResult? {
    return try {
        val amount = initialAmount.toDoubleOrNull() ?: return null
        val rate = gstRate.toDoubleOrNull() ?: return null
        
        val baseAmount: Double
        val gstAmount: Double
        val totalAmount: Double
        
        if (gstOperation == addGstLabel) {
            // Add GST: Base Amount is the input, GST is added
            baseAmount = amount
            gstAmount = baseAmount * (rate / 100)
            totalAmount = baseAmount + gstAmount
        } else {
            // Remove GST: Input is the total amount (including GST)
            totalAmount = amount
            baseAmount = totalAmount / (1 + (rate / 100))
            gstAmount = totalAmount - baseAmount
        }
        
        // CGST and SGST are each half of GST
        val cgstRate = rate / 2
        val sgstRate = rate / 2
        val cgstAmount = gstAmount / 2
        val sgstAmount = gstAmount / 2
        
        GSTResult(
            initialAmount = baseAmount,
            gstAmount = gstAmount,
            totalAmount = totalAmount,
            cgstAmount = cgstAmount,
            sgstAmount = sgstAmount,
            cgstRate = cgstRate,
            sgstRate = sgstRate
        )
    } catch (e: Exception) {
        null
    }
}



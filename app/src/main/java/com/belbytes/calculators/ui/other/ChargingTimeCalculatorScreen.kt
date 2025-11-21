package com.belbytes.calculators.ui.other

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import kotlin.math.floor

data class ChargingTimeResult(
    val batteryCapacity: Double,
    val chargerOutput: Double,
    val chargingTimeHours: Double,
    val chargingTimeHoursInt: Int,
    val chargingTimeMinutes: Int
)

@Composable
fun ChargingTimeCalculatorScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    // State variables
    var batteryCapacity by rememberSaveable { mutableStateOf("") }
    var chargerOutput by rememberSaveable { mutableStateOf("") }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var chargingTimeResult by remember { mutableStateOf<ChargingTimeResult?>(null) }
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
        ChargingTimeCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Battery Capacity Input
            ChargingTimeInputField(
                label = context.getString(R.string.battery_capacity),
                placeholder = context.getString(R.string.placeholder_amount),
                value = batteryCapacity,
                onValueChange = { batteryCapacity = it }
            )

            // Charger Output Input
            ChargingTimeInputField(
                label = context.getString(R.string.charger_output),
                placeholder = context.getString(R.string.placeholder_rate),
                value = chargerOutput,
                onValueChange = { chargerOutput = it }
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
                        keyboardController?.hide() // Hide keyboard when calculate is clicked
                        errorMessage = null
                        when {
                            batteryCapacity.isBlank() || (batteryCapacity.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = context.getString(R.string.error_invalid_amount)
                                showResults = false
                                chargingTimeResult = null
                            }
                            chargerOutput.isBlank() || (chargerOutput.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = context.getString(R.string.error_invalid_rate)
                                showResults = false
                                chargingTimeResult = null
                            }
                            else -> {
                                val result = calculateChargingTime(batteryCapacity, chargerOutput)
                                if (result != null) {
                                    chargingTimeResult = result
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    errorMessage = context.getString(R.string.error_check_inputs)
                                    showResults = false
                                    chargingTimeResult = null
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
                        batteryCapacity = ""
                        chargerOutput = ""
                        showResults = false
                        chargingTimeResult = null
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
                        text = context.getString(R.string.reset),
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
                visible = showResults && chargingTimeResult != null,
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
                chargingTimeResult?.let { result ->
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
                            // Battery Capacity
                            ChargingTimeResultRow(
                                "Battery Capacity (mAh)",
                                formatNumberWithDecimal(result.batteryCapacity)
                            )
                            
                            // Charger Output
                            ChargingTimeResultRow(
                                "Charger Output (mA)",
                                formatNumberWithDecimal(result.chargerOutput)
                            )
                            
                            // Charging Time
                            ChargingTimeResultRow(
                                "Charging Time",
                                "${result.chargingTimeHoursInt} hr ${result.chargingTimeMinutes} min"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChargingTimeCalculatorHeader(onBackClick: () -> Unit) {
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
            text = context.getString(R.string.charging_time_calculator),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChargingTimeInputField(
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
fun ChargingTimeResultRow(
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

fun calculateChargingTime(
    batteryCapacity: String,
    chargerOutput: String
): ChargingTimeResult? {
    return try {
        val capacity = batteryCapacity.toDoubleOrNull() ?: return null
        val output = chargerOutput.toDoubleOrNull() ?: return null
        
        if (output == 0.0) return null // Avoid division by zero
        
        // Calculate charging time in hours
        val chargingTimeHours = capacity / output
        
        // Extract hours and minutes
        val hours = floor(chargingTimeHours).toInt()
        val minutes = ((chargingTimeHours - hours) * 60).toInt()
        
        ChargingTimeResult(
            batteryCapacity = capacity,
            chargerOutput = output,
            chargingTimeHours = chargingTimeHours,
            chargingTimeHoursInt = hours,
            chargingTimeMinutes = minutes
        )
    } catch (e: Exception) {
        null
    }
}

fun formatNumberWithDecimal(value: Double): String {
    // Format with decimal if needed, otherwise show as integer
    return if (value % 1.0 == 0.0) {
        String.format("%.0f", value)
    } else {
        String.format("%.1f", value)
    }
}


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
import com.belbytes.calculators.utils.formatCurrencyWithDecimal

data class VATResult(
    val initialAmount: Double,
    val vatAmount: Double,
    val totalAmount: Double
)

@Composable
fun VATCalculatorScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    
    // State variables
    var initialAmount by rememberSaveable { mutableStateOf("") }
    var vatRate by rememberSaveable { mutableStateOf("") }
    var vatOperation by rememberSaveable { mutableStateOf("Add VAT (+)") } // "Add VAT (+)" or "Remove VAT (-)"
    var showResults by rememberSaveable { mutableStateOf(false) }
    var vatResult by remember { mutableStateOf<VATResult?>(null) }
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
        VATCalculatorHeader(onBackClick = onBackClick)

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
            VATInputField(
                label = "Amount",
                placeholder = "Ex: 10000",
                value = initialAmount,
                onValueChange = { initialAmount = it }
            )

            // Rate of VAT Input
            VATInputField(
                label = "Rate of VAT",
                placeholder = "Ex: 6.5",
                value = vatRate,
                onValueChange = { vatRate = it }
            )

            // VAT Operation Radio Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                VATRadioButton(
                    label = "Add VAT (+)",
                    selected = vatOperation == "Add VAT (+)",
                    onClick = { vatOperation = "Add VAT (+)" }
                )
                VATRadioButton(
                    label = "Remove VAT (-)",
                    selected = vatOperation == "Remove VAT (-)",
                    onClick = { vatOperation = "Remove VAT (-)" }
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
                                errorMessage = "Please enter a valid amount"
                                showResults = false
                                vatResult = null
                            }
                            vatRate.isBlank() || (vatRate.toDoubleOrNull() ?: -1.0) <= 0 -> {
                                errorMessage = "Please enter a valid VAT rate"
                                showResults = false
                                vatResult = null
                            }
                            else -> {
                                val result = calculateVAT(initialAmount, vatRate, vatOperation)
                                if (result != null) {
                                    vatResult = result
                                    showResults = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Please check all input values"
                                    showResults = false
                                    vatResult = null
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
                        initialAmount = ""
                        vatRate = ""
                        vatOperation = "Add VAT (+)"
                        showResults = false
                        vatResult = null
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

            // Results Section Card
            AnimatedVisibility(
                visible = showResults && vatResult != null,
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
                vatResult?.let { result ->
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
                            VATResultRow("Initial Amount", formatCurrencyWithDecimal(context, result.initialAmount))
                            
                            // VAT Amount
                            VATResultRow("VAT Amount", formatCurrencyWithDecimal(context, result.vatAmount))
                            
                            // Total Amount
                            VATResultRow("Total Amount", formatCurrencyWithDecimal(context, result.totalAmount))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VATCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "VAT Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VATInputField(
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
fun VATRadioButton(
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
fun VATResultRow(
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

fun calculateVAT(
    initialAmount: String,
    vatRate: String,
    vatOperation: String
): VATResult? {
    return try {
        val amount = initialAmount.toDoubleOrNull() ?: return null
        val rate = vatRate.toDoubleOrNull() ?: return null
        
        val baseAmount: Double
        val vatAmount: Double
        val totalAmount: Double
        
        if (vatOperation == "Add VAT (+)") {
            // Add VAT: Base Amount is the input, VAT is added
            baseAmount = amount
            vatAmount = baseAmount * (rate / 100)
            totalAmount = baseAmount + vatAmount
        } else {
            // Remove VAT: Input is the total amount (including VAT)
            totalAmount = amount
            baseAmount = totalAmount / (1 + (rate / 100))
            vatAmount = totalAmount - baseAmount
        }
        
        VATResult(
            initialAmount = baseAmount,
            vatAmount = vatAmount,
            totalAmount = totalAmount
        )
    } catch (e: Exception) {
        null
    }
}


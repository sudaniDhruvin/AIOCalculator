package com.belbytes.calculators.ui.other

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DiscountResult(
    val amount: Double,
    val salesTax: Double,
    val saving: Double,
    val payableAmount: Double
)

@Composable
fun DiscountCalculatorScreen(
    onBackClick: () -> Unit
) {
    // State variables
    var amount by rememberSaveable { mutableStateOf("") }
    var discountPercent by rememberSaveable { mutableStateOf("") }
    var salesTaxPercent by rememberSaveable { mutableStateOf("") }
    var applyDiscount by rememberSaveable { mutableStateOf("After Tax") } // "After Tax" or "Before Tax"
    var showResults by rememberSaveable { mutableStateOf(false) }
    var discountResult by rememberSaveable { mutableStateOf<DiscountResult?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        DiscountCalculatorHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Amount Input
            DiscountInputField(
                label = "Amount",
                placeholder = "Ex: 20000",
                value = amount,
                onValueChange = { amount = it }
            )

            // Discount % Input
            DiscountInputField(
                label = "Discount %",
                placeholder = "Ex: 8.5",
                value = discountPercent,
                onValueChange = { discountPercent = it }
            )

            // Sales Tax % Input
            DiscountInputField(
                label = "Sales Tax %",
                placeholder = "Ex: 6.0",
                value = salesTaxPercent,
                onValueChange = { salesTaxPercent = it }
            )

            // Apply Discount Radio Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                DiscountRadioButton(
                    label = "After Tax",
                    selected = applyDiscount == "After Tax",
                    onClick = { applyDiscount = "After Tax" }
                )
                DiscountRadioButton(
                    label = "Before Tax",
                    selected = applyDiscount == "Before Tax",
                    onClick = { applyDiscount = "Before Tax" }
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
                        val result = calculateDiscount(
                            amount,
                            discountPercent,
                            salesTaxPercent,
                            applyDiscount
                        )
                        if (result != null) {
                            discountResult = result
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
                        amount = ""
                        discountPercent = ""
                        salesTaxPercent = ""
                        applyDiscount = "After Tax"
                        showResults = false
                        discountResult = null
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
                visible = showResults && discountResult != null,
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
                discountResult?.let { result ->
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
                            // Amount
                            DiscountResultRow("Amount", formatCurrencyWithDecimal(result.amount))
                            
                            // Sales Tax
                            DiscountResultRow("Sales Tax", formatCurrencyWithDecimal(result.salesTax))
                            
                            // Saving
                            DiscountResultRow("Saving", formatCurrencyWithDecimal(result.saving))
                            
                            // Payable Amount
                            DiscountResultRow("Payable Amount", formatCurrencyWithDecimal(result.payableAmount))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiscountCalculatorHeader(onBackClick: () -> Unit) {
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
            text = "Discount Calculator",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DiscountInputField(
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
                    color = Color.Black,
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
            singleLine = true
        )
    }
}

@Composable
fun DiscountRadioButton(
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
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun DiscountResultRow(
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

fun calculateDiscount(
    amount: String,
    discountPercent: String,
    salesTaxPercent: String,
    applyDiscount: String
): DiscountResult? {
    return try {
        val amountValue = amount.toDoubleOrNull() ?: return null
        val discountPercentValue = discountPercent.toDoubleOrNull() ?: return null
        val salesTaxPercentValue = salesTaxPercent.toDoubleOrNull() ?: return null
        
        val salesTax: Double
        val saving: Double
        val payableAmount: Double
        
        if (applyDiscount == "After Tax") {
            // Apply discount first, then add tax
            val discountAmount = amountValue * (discountPercentValue / 100)
            val discountedAmount = amountValue - discountAmount
            salesTax = discountedAmount * (salesTaxPercentValue / 100)
            payableAmount = discountedAmount + salesTax
            saving = discountAmount
        } else {
            // Add tax first, then apply discount
            val taxAmount = amountValue * (salesTaxPercentValue / 100)
            val amountWithTax = amountValue + taxAmount
            val discountAmount = amountWithTax * (discountPercentValue / 100)
            payableAmount = amountWithTax - discountAmount
            salesTax = taxAmount
            saving = discountAmount
        }
        
        DiscountResult(
            amount = amountValue,
            salesTax = salesTax,
            saving = saving,
            payableAmount = payableAmount
        )
    } catch (e: Exception) {
        null
    }
}

fun formatCurrencyWithDecimal(amount: Double): String {
    return String.format("%,.2f", amount)
}


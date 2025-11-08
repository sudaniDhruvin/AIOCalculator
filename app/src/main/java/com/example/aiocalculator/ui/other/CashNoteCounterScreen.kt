package com.example.aiocalculator.ui.other

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CashNoteCounterResult(
    val totalAmount: Double
)

@Composable
fun CashNoteCounterScreen(
    onBackClick: () -> Unit
) {
    // State variables for each denomination
    var note2000 by rememberSaveable { mutableStateOf("") }
    var note500 by rememberSaveable { mutableStateOf("") }
    var note200 by rememberSaveable { mutableStateOf("") }
    var note100 by rememberSaveable { mutableStateOf("") }
    var note50 by rememberSaveable { mutableStateOf("") }
    var note20 by rememberSaveable { mutableStateOf("") }
    var note10 by rememberSaveable { mutableStateOf("") }
    var note5 by rememberSaveable { mutableStateOf("") }
    var note2 by rememberSaveable { mutableStateOf("") }
    var note1 by rememberSaveable { mutableStateOf("") }
    
    var showResults by rememberSaveable { mutableStateOf(false) }
    var totalAmount by rememberSaveable { mutableStateOf(0.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        CashNoteCounterHeader(onBackClick = onBackClick)

        // Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Denomination Rows
            CashNoteRow(
                denomination = 2000,
                value = note2000,
                onValueChange = { note2000 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 500,
                value = note500,
                onValueChange = { note500 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 200,
                value = note200,
                onValueChange = { note200 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 100,
                value = note100,
                onValueChange = { note100 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 50,
                value = note50,
                onValueChange = { note50 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 20,
                value = note20,
                onValueChange = { note20 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 10,
                value = note10,
                onValueChange = { note10 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 5,
                value = note5,
                onValueChange = { note5 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 2,
                value = note2,
                onValueChange = { note2 = it },
                onSubtotalChange = { }
            )
            
            CashNoteRow(
                denomination = 1,
                value = note1,
                onValueChange = { note1 = it },
                onSubtotalChange = { }
            )

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calculate Button
                Button(
                    onClick = {
                        val result = calculateCashTotal(
                            note2000, note500, note200, note100, note50,
                            note20, note10, note5, note2, note1
                        )
                        totalAmount = result
                        showResults = true
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
                        note2000 = ""
                        note500 = ""
                        note200 = ""
                        note100 = ""
                        note50 = ""
                        note20 = ""
                        note10 = ""
                        note5 = ""
                        note2 = ""
                        note1 = ""
                        showResults = false
                        totalAmount = 0.0
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

            // Final Answer Section
            AnimatedVisibility(
                visible = showResults,
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
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF7F7F7)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Final Answer:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                        Text(
                            text = if (totalAmount % 1.0 == 0.0) {
                                String.format("%.0f", totalAmount)
                            } else {
                                String.format("%,.2f", totalAmount)
                            },
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

@Composable
fun CashNoteCounterHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF4257B2))
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
            text = "Cash Note Counter",
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
fun CashNoteRow(
    denomination: Int,
    value: String,
    onValueChange: (String) -> Unit,
    onSubtotalChange: (Double) -> Unit
) {
    val quantity = value.toDoubleOrNull() ?: 0.0
    val subtotal = denomination * quantity
    
    // Calculate subtotal when value changes
    LaunchedEffect(value) {
        onSubtotalChange(subtotal)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Denomination
        Text(
            text = denomination.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.width(40.dp)
        )
        
        // Multiplication symbol
        Text(
            text = "x",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        // Input field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                textAlign = TextAlign.Start
            )
        )
        
        // Equals symbol
        Text(
            text = "=",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        // Subtotal
        Text(
            text = formatNumber(subtotal),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Start
        )
    }
}

fun calculateCashTotal(
    note2000: String,
    note500: String,
    note200: String,
    note100: String,
    note50: String,
    note20: String,
    note10: String,
    note5: String,
    note2: String,
    note1: String
): Double {
    val total = (note2000.toDoubleOrNull() ?: 0.0) * 2000 +
                (note500.toDoubleOrNull() ?: 0.0) * 500 +
                (note200.toDoubleOrNull() ?: 0.0) * 200 +
                (note100.toDoubleOrNull() ?: 0.0) * 100 +
                (note50.toDoubleOrNull() ?: 0.0) * 50 +
                (note20.toDoubleOrNull() ?: 0.0) * 20 +
                (note10.toDoubleOrNull() ?: 0.0) * 10 +
                (note5.toDoubleOrNull() ?: 0.0) * 5 +
                (note2.toDoubleOrNull() ?: 0.0) * 2 +
                (note1.toDoubleOrNull() ?: 0.0) * 1
    
    return total
}

fun formatNumber(value: Double): String {
    return if (value % 1.0 == 0.0) {
        String.format("%.0f", value)
    } else {
        String.format("%.2f", value)
    }
}


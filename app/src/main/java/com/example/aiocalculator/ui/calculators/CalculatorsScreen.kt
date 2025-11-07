package com.example.aiocalculator.ui.calculators

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.aiocalculator.R
import com.example.aiocalculator.data.DataRepository
import com.example.aiocalculator.data.CalculatorItemData

@Composable
fun CalculatorsScreen(
    onBackClick: () -> Unit = {},
    onCalculatorClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var featuredTools by remember { mutableStateOf<List<com.example.aiocalculator.data.FeaturedTool>>(emptyList()) }

    LaunchedEffect(Unit) {
        val data = DataRepository.loadAppData(context)
        featuredTools = data.featuredTools
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header Section - centered "Calculators" text, no back arrow
        HeaderSection()
        
        // Calculators List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            featuredTools.forEach { featuredTool ->
                item {
                    CalculatorCategorySection(
                        categoryTitle = featuredTool.name,
                        calculatorItems = featuredTool.calculatorItems,
                        onCalculatorClick = { calculatorId ->
                            onCalculatorClick(calculatorId)
                        }
                    )
                }
            }
            
            // Bottom padding for navigation bar
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(0xFF2196F3))
    ) {
        Text(
            text = "Calculators",
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
fun CalculatorCategorySection(
    categoryTitle: String,
    calculatorItems: List<CalculatorItemData>,
    onCalculatorClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Category Title
        Text(
            text = categoryTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Calculator Grid - use same card design as CommonCalculatorCategoryScreen
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Special layout for EMI Calculators (3 cards in first row, 1 in second row)
            if (categoryTitle == "EMI Calculators" && calculatorItems.size == 4) {
                // First row with 3 cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    calculatorItems.take(3).forEach { calculator ->
                        CalculatorCard(
                            calculator = calculator,
                            onClick = { onCalculatorClick(calculator.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Second row with 1 card - starts at same position as first row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    calculatorItems.drop(3).take(1).forEach { calculator ->
                        CalculatorCard(
                            calculator = calculator,
                            onClick = { onCalculatorClick(calculator.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Add empty spaces to maintain row structure
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                // Default layout: Display calculators in rows of 3
                calculatorItems.chunked(3).forEachIndexed { rowIndex, rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { calculator ->
                            CalculatorCard(
                                calculator = calculator,
                                onClick = { onCalculatorClick(calculator.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Add empty spaces if row has less than 3 items
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    
                    // Add spacing between rows
                    if (rowIndex < calculatorItems.chunked(3).size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorCard(
    calculator: CalculatorItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(80.dp)
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = getIconResourceForCalculatorsScreen(calculator.iconName)),
                contentDescription = calculator.name,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = calculator.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                overflow = TextOverflow.Visible
            )
        }
    }
}

fun getIconResourceForCalculatorsScreen(iconName: String): Int {
    return when (iconName) {
        "emi_calculator" -> R.drawable.green_calce
        "quick_calculator" -> R.drawable.orange_cal
        "advance_emi" -> R.drawable.more_calce
        "compare_loans" -> R.drawable.compare
        "sip_calculator" -> R.drawable.spi
        "quick_sip" -> R.drawable.sip_cal
        "advance_sip" -> R.drawable.advance
        "compare_sip" -> R.drawable.compare_sip
        "swp_calculator" -> R.drawable.swp
        "stp_calculator" -> R.drawable.stp
        "loan_profile" -> R.drawable.loan_profile
        "prepayment_roi" -> R.drawable.pre_payment
        "check_eligibility" -> R.drawable.check_eligibility
        "moratorium" -> R.drawable.moratorium
        "fd_calculator" -> R.drawable.fd
        "rd_calculator" -> R.drawable.rd
        "ppf_calculator" -> R.drawable.ppf
        "simple_interest" -> R.drawable.simple_interest
        "gst_calculator" -> R.drawable.gst
        "vat_calculator" -> R.drawable.vat
        "discount_calculator" -> R.drawable.discount
        "cash_note_counter" -> R.drawable.cash_note
        "charging_time" -> R.drawable.charging
        else -> R.drawable.calculator
    }
}

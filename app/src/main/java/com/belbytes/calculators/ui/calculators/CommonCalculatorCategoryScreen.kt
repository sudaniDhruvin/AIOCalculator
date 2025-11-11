package com.belbytes.calculators.ui.calculators

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.belbytes.calculators.R
import com.belbytes.calculators.data.CalculatorItemData
import com.belbytes.calculators.data.DataRepository

@Composable
fun CommonCalculatorCategoryScreen(
    route: String,
    onBackClick: () -> Unit = {},
    onCalculatorClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var calculatorItems by remember { mutableStateOf<List<CalculatorItemData>>(emptyList()) }

    LaunchedEffect(route) {
        val data = DataRepository.loadAppData(context)
        val featuredTool = DataRepository.getFeaturedToolByRoute(route)
        title = featuredTool?.name ?: ""
        calculatorItems = featuredTool?.calculatorItems ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CalculatorCategoryHeader(
            title = title,
            onBackClick = onBackClick
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            if (route == "/emi_calculator" && calculatorItems.size == 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    calculatorItems.take(3).forEach { calculator ->
                        CalculatorCategoryCard(
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
                        CalculatorCategoryCard(
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
                calculatorItems.chunked(3).forEachIndexed { rowIndex, rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { calculator ->
                            CalculatorCategoryCard(
                                calculator = calculator,
                                onClick = { onCalculatorClick(calculator.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    
                    if (rowIndex < calculatorItems.chunked(3).size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorCategoryHeader(
    title: String,
    onBackClick: () -> Unit
) {
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
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CalculatorCategoryCard(
    calculator: CalculatorItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = Color(android.graphics.Color.parseColor(calculator.color))
    val lightCardColor = lightenColorForCard(iconColor)
    
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = lightCardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = iconColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getWhiteIconResourceForCalculator(calculator.iconName)),
                    contentDescription = calculator.name,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = calculator.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                overflow = TextOverflow.Visible
            )
        }
    }
}

fun lightenColorForCard(color: Color): Color {
    val lightenFactor = 0.9f
    val r = (color.red * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    val g = (color.green * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    val b = (color.blue * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    return Color(r, g, b, color.alpha)
}

fun getWhiteIconResourceForCalculator(iconName: String): Int {
    return when (iconName) {
        "emi_calculator" -> R.drawable.white_dollar_cal
        "quick_calculator" -> R.drawable.white_quick
        "advance_emi" -> R.drawable.white_advance
        "compare_loans" -> R.drawable.white_compare
        "sip_calculator" -> R.drawable.white_cal
        "quick_sip" -> R.drawable.white_quick_sip
        "advance_sip" -> R.drawable.advance_sip
        "compare_sip" -> R.drawable.white_compare_sip
        "swp_calculator" -> R.drawable.white_swp
        "stp_calculator" -> R.drawable.stp_white
        "loan_profile" -> R.drawable.profile_loan
        "prepayment_roi" -> R.drawable.roi_change
        "check_eligibility" -> R.drawable.eligibility_check
        "moratorium" -> R.drawable.moratorium_calculator
        "fd_calculator" -> R.drawable.fd_cal
        "rd_calculator" -> R.drawable.rd_cal
        "ppf_calculator" -> R.drawable.ppf_cal
        "simple_interest" -> R.drawable.white_simple_rate
        "gst_calculator" -> R.drawable.white_gst
        "vat_calculator" -> R.drawable.white_vat
        "discount_calculator" -> R.drawable.white_cach_note
        "cash_note_counter" -> R.drawable.white_discount
        "charging_time" -> R.drawable.white_charge
        else -> {
            when (iconName) {
                "emi_calculator" -> R.drawable.loan
                "quick_calculator" -> R.drawable.cal
                "advance_emi" -> R.drawable.advance
                "compare_loans" -> R.drawable.compare
                "sip_calculator" -> R.drawable.sip_cal
                "quick_sip" -> R.drawable.white_quick_sip
                "advance_sip" -> R.drawable.advance_sip
                "compare_sip" -> R.drawable.compare_sip
                "swp_calculator" -> R.drawable.swp
                "stp_calculator" -> R.drawable.stp
                "loan_profile" -> R.drawable.loan_profile
                "prepayment_roi" -> R.drawable.roi_change
                "check_eligibility" -> R.drawable.check_eligibility
                "moratorium" -> R.drawable.moratorium
                "fd_calculator" -> R.drawable.fd_cal
                "rd_calculator" -> R.drawable.rd_cal
                "ppf_calculator" -> R.drawable.ppf_cal
                "simple_interest" -> R.drawable.simple_interest
                "gst_calculator" -> R.drawable.gst
                "vat_calculator" -> R.drawable.vat
                "discount_calculator" -> R.drawable.discount
                "cash_note_counter" -> R.drawable.cash_note
                "charging_time" -> R.drawable.charging
                else -> R.drawable.calculator
            }
        }
    }
}


package com.example.aiocalculator.ui.calculators

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.aiocalculator.R

data class CalculatorItem(
    val id: String,
    val name: String,
    val iconName: String,
    val color: String
)

data class CalculatorCategory(
    val id: String,
    val title: String,
    val items: List<CalculatorItem>
)

@Composable
fun CalculatorsScreen(
    onBackClick: () -> Unit = {}
) {
    val categories = getCalculatorCategories()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header Section
        HeaderSection()
        
        // Calculators List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            categories.forEach { category ->
                item {
                    CalculatorCategorySection(
                        category = category,
                        onCalculatorClick = { calculator ->
                            // Handle calculator click
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
            .background(Color(0xFF2196F3)) // Blue color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp).padding(top = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calculators",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CalculatorCategorySection(
    category: CalculatorCategory,
    onCalculatorClick: (CalculatorItem) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Category Title
        Text(
            text = category.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Calculator Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Display calculators in rows of 3
            category.items.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { calculator ->
                        CalculatorCard(
                            calculator = calculator,
                            onClick = { onCalculatorClick(calculator) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Add empty spaces if row has less than 3 items
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorCard(
    calculator: CalculatorItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(80.dp)
            .height(85.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Icon at top - fixed position
            Spacer(modifier = Modifier.height(4.dp))
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(calculator.color)),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getIconResourceForCalculator(calculator.iconName)),
                    contentDescription = calculator.name,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Text below icon - fixed height container for consistent alignment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = calculator.name,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2,
                    lineHeight = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun getIconForCalculator(iconName: String): ImageVector {
    return when (iconName) {
        "emi_calculator" -> Icons.Default.Home
        "quick_calculator" -> Icons.Default.Home
        "advance_emi" -> Icons.Default.Home
        "compare_loans" -> Icons.Default.Home
        "sip_calculator" -> Icons.Default.Home
        "quick_sip" -> Icons.Default.Home
        "advance_sip" -> Icons.Default.Home
        "compare_sip" -> Icons.Default.Home
        "swp_calculator" -> Icons.Default.Home
        "stp_calculator" -> Icons.Default.Home
        "loan_profile" -> Icons.Default.Home
        "prepayment_roi" -> Icons.Default.Home
        "check_eligibility" -> Icons.Default.CheckCircle
        "moratorium" -> Icons.Default.Home
        "fd_calculator" -> Icons.Default.Lock
        "rd_calculator" -> Icons.Default.Home
        "ppf_calculator" -> Icons.Default.Home
        "simple_interest" -> Icons.Default.Home
        "gst_calculator" -> Icons.Default.Home
        "vat_calculator" -> Icons.Default.Home
        "discount_calculator" -> Icons.Default.Home
        "cash_note_counter" -> Icons.Default.Home
        "charging_time" -> Icons.Default.Home
        else -> Icons.Default.Home
    }
}

fun getIconResourceForCalculator(iconName: String): Int {
    return when (iconName) {
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

fun getCalculatorCategories(): List<CalculatorCategory> {
    return listOf(
        CalculatorCategory(
            id = "emi",
            title = "EMI Calculators",
            items = listOf(
                CalculatorItem("1", "EMI Calculator", "emi_calculator", "#4CAF50"),
                CalculatorItem("2", "Quick Calculator", "quick_calculator", "#FF9800"),
                CalculatorItem("3", "Advance EMI", "advance_emi", "#9C27B0"),
                CalculatorItem("4", "Compare Loans", "compare_loans", "#2196F3")
            )
        ),
        CalculatorCategory(
            id = "sip",
            title = "SIP Calculators",
            items = listOf(
                CalculatorItem("5", "SIP Calculator", "sip_calculator", "#9C27B0"),
                CalculatorItem("6", "Quick SIP Calculator", "quick_sip", "#FF9800"),
                CalculatorItem("7", "Advance SIP", "advance_sip", "#4CAF50"),
                CalculatorItem("8", "Compare SIP", "compare_sip", "#2196F3"),
                CalculatorItem("9", "SWP Calculator", "swp_calculator", "#F44336"),
                CalculatorItem("10", "STP Calculator", "stp_calculator", "#4CAF50")
            )
        ),
        CalculatorCategory(
            id = "loan",
            title = "Loan Calculators",
            items = listOf(
                CalculatorItem("11", "Loan Profile", "loan_profile", "#FF9800"),
                CalculatorItem("12", "Pre Payment ROI Change", "prepayment_roi", "#03A9F4"),
                CalculatorItem("13", "Check Eligibility", "check_eligibility", "#9C27B0"),
                CalculatorItem("14", "Moratorium Calculator", "moratorium", "#03A9F4")
            )
        ),
        CalculatorCategory(
            id = "bank",
            title = "Bank Calculators",
            items = listOf(
                CalculatorItem("15", "FD Calculator", "fd_calculator", "#795548"),
                CalculatorItem("16", "RD Calculator", "rd_calculator", "#E91E63"),
                CalculatorItem("17", "PPF Calculator", "ppf_calculator", "#9C27B0"),
                CalculatorItem("18", "Simple Interest", "simple_interest", "#4CAF50")
            )
        ),
        CalculatorCategory(
            id = "gst",
            title = "GST & VAT",
            items = listOf(
                CalculatorItem("19", "GST Calculator", "gst_calculator", "#4CAF50"),
                CalculatorItem("20", "VAT Calculator", "vat_calculator", "#FF9800")
            )
        ),
        CalculatorCategory(
            id = "other",
            title = "Other Calculators",
            items = listOf(
                CalculatorItem("21", "Discount Calculator", "discount_calculator", "#9C27B0"),
                CalculatorItem("22", "Cash Note Counter", "cash_note_counter", "#2196F3"),
                CalculatorItem("23", "Charging Time", "charging_time", "#FF9800")
            )
        )
    )
}

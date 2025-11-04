package com.example.aiocalculator.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiocalculator.data.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.aiocalculator.R

@Composable
fun HomeScreen(
    onFeaturedToolClick: (String) -> Unit = {},
    onRecentCalculationClick: (String) -> Unit = {},
    onViewAllFeatured: () -> Unit = {},
    onViewAllRecent: () -> Unit = {}
) {
    val context = LocalContext.current
    var featuredTools by remember { mutableStateOf<List<FeaturedTool>>(emptyList()) }
    var recentCalculations by remember { mutableStateOf<List<RecentCalculation>>(emptyList()) }

    LaunchedEffect(Unit) {
        val data = DataRepository.loadAppData(context)
        featuredTools = data.featuredTools
        recentCalculations = data.recentCalculations
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header Section
        item {
            HeaderSection()
        }

        // Featured Tools Section
        item {
            FeaturedToolsSection(
                tools = featuredTools,
                onToolClick = onFeaturedToolClick,
                onViewAll = onViewAllFeatured
            )
        }

        // Recent Calculations Section
        item {
            RecentCalculationsSection(
                calculations = recentCalculations,
                onCalculationClick = onRecentCalculationClick,
                onViewAll = onViewAllRecent
            )
        }

        // Bottom padding for navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
            .background(Color(0xFF2196F3)) // Blue color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "All in one calculators",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FeaturedToolsSection(
    tools: List<FeaturedTool>,
    onToolClick: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Tools",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Use Column with Row instead of LazyVerticalGrid to avoid nested lazy lists
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // First row of 3 items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tools.take(3).forEach { tool ->
                    FeaturedToolCard(
                        tool = tool,
                        onClick = { onToolClick(tool.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Second row of 3 items (if available)
            if (tools.size > 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    tools.drop(3).take(3).forEach { tool ->
                        FeaturedToolCard(
                            tool = tool,
                            onClick = { onToolClick(tool.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedToolCard(
    tool: FeaturedTool,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Icon at top - fixed position
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(tool.color)),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getIconResourceForTool(tool.iconName)),
                    contentDescription = tool.name,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Text below icon - fixed height container for consistent alignment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatToolName(tool.name),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun formatToolName(name: String): String {
    // Format names like "EMI Calculators" to "EMI\nCalculators"
    return when {
        name.contains("Calculators") -> {
            val parts = name.split(" ")
            if (parts.size >= 2) {
                parts[0] + "\n" + parts[1]
            } else {
                name
            }
        }
        name.contains("&") -> name // GST & VAT stays as is
        else -> name
    }
}

@Composable
fun RecentCalculationsSection(
    calculations: List<RecentCalculation>,
    onCalculationClick: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Calculations",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Use Column instead of LazyColumn to avoid nested lazy lists
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            calculations.forEach { calculation ->
                RecentCalculationCard(
                    calculation = calculation,
                    onClick = { onCalculationClick(calculation.detailsRoute) }
                )
            }
        }
    }
}

@Composable
fun RecentCalculationCard(
    calculation: RecentCalculation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(calculation.color)),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getIconResourceForTool(calculation.iconName)),
                    contentDescription = calculation.calculatorType,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = calculation.calculatorType,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = calculation.date,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Arrow icon
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Navigate",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun getIconForTool(iconName: String): ImageVector {
    return when (iconName) {
        "ic_emi_calculator" -> Icons.Default.ShoppingCart
        "ic_sip_calculator" -> Icons.Default.Add
        "ic_loan_calculator" -> Icons.Default.Home
        "ic_bank_calculator" -> Icons.Default.Home
        "ic_gst_vat" -> Icons.Default.Home
        "ic_other_calculators" -> Icons.Default.Home
        else -> Icons.Default.Home
    }
}

fun getIconResourceForTool(iconName: String): Int {
    return when (iconName) {
        "ic_emi_calculator" -> R.drawable.white_dollar_cal
        "ic_sip_calculator" -> R.drawable.white_cal
        "ic_loan_calculator" -> R.drawable.loan_percent
        "ic_bank_calculator" -> R.drawable.home_percent
        "ic_gst_vat" -> R.drawable.gst_percent
        "ic_other_calculators" -> R.drawable.calculator
        else -> R.drawable.calculator
    }
}


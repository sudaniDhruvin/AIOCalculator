package com.belbytes.calculators.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belbytes.calculators.data.*
import com.belbytes.calculators.data.local.AppDatabase
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.belbytes.calculators.R
import com.belbytes.calculators.ads.BannerAd
import com.belbytes.calculators.ads.NativeAd

@Composable
fun HomeScreen(
    onFeaturedToolClick: (String) -> Unit = {},
    onRecentCalculationClick: (String) -> Unit = {},
    onViewAllFeatured: () -> Unit = {},
    onViewAllRecent: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel {
        val database = AppDatabase.getDatabase(context)
        val recentCalculationRepository = RecentCalculationRepository(database)
        HomeViewModel(context, DataRepository, recentCalculationRepository)
    }
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAFAFA)) // Off-white background
            ) {
                // Header Section
                item {
                    HeaderSection()
                }

                // Featured Tools Section
                item {
                    FeaturedToolsSection(
                        tools = uiState.featuredTools,
                        onToolClick = onFeaturedToolClick,
                        onViewAll = onViewAllFeatured
                    )
                }
                
                // Banner Ad
                item {
                    BannerAd(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Recent Calculations Section
                item {
                    RecentCalculationsSection(
                        calculations = uiState.recentCalculations,
                        onCalculationClick = onRecentCalculationClick,
                        onViewAll = onViewAllRecent
                    )
                }
                
                // Native Ad
                item {
                    NativeAd(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Bottom padding for navigation bar
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .background(Color(0xFF2196F3)) // Blue color
            .statusBarsPadding()
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
            verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing
        ) {
            // First row of 3 items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Match vertical spacing
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Match vertical spacing
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
    val iconColor = Color(android.graphics.Color.parseColor(tool.color))
    val lightCardColor = lightenColorForCard(iconColor)
    
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Subtle shadow
        colors = CardDefaults.cardColors(containerColor = lightCardColor) // Light version of icon color
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
                        color = iconColor,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getIconResourceForTool(tool.iconName)),
                    contentDescription = tool.name,
                    modifier = Modifier.size(24.dp)
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
                    fontWeight = FontWeight.Normal,
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
            if (calculations.isNotEmpty()) {
                Text(
                    text = "View All",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onViewAll() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show empty state if no calculations
        if (calculations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Empty state icon - composite icon showing calculator, document, and chart
                    Image(
                        painter = painterResource(id = R.drawable.no_data),
                        contentDescription = "No calculations",
                        modifier = Modifier.size(76.dp)
                    )
                    
                    Text(
                        text = "No Any Calculations",
                        fontSize = 15.sp,
                        color = Color(0xFF919191), // Light grey color
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        } else {
            // Use Column instead of LazyColumn to avoid nested lazy lists
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing
            ) {
                calculations.forEach { calculation ->
                    RecentCalculationCard(
                        calculation = calculation,
                        onClick = { onCalculationClick(calculation.getEffectiveCalculatorId()) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecentCalculationCard(
    calculation: RecentCalculation,
    onClick: () -> Unit
) {
    val baseColor = Color(android.graphics.Color.parseColor(calculation.color))
    val lightIconContainerColor = lightenIconContainerColor(baseColor) // Light color for container
    val darkIconColor = darkenIconColor(baseColor) // Dark color for icon
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Subtle shadow
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // Uniform very light off-white
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with light background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = lightIconContainerColor,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Use Image with colorFilter to tint the icon dark
                Image(
                    painter = painterResource(id = getIconResourceForRecentCalculation(calculation.iconName)),
                    contentDescription = calculation.calculatorType,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(darkIconColor)
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
                    fontWeight = FontWeight.Bold, // Bold text
                    color = Color.Black
                )
                Text(
                    text = calculation.date,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Chevron arrow icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color(0xFF616161), // Dark gray
                modifier = Modifier.size(20.dp)
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

// Helper function to create a very light version of the icon color for card background
fun lightenColorForCard(color: Color): Color {
    // Mix with white (90% white, 10% original color) to create a very light tint
    val lightenFactor = 0.9f
    val r = (color.red * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    val g = (color.green * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    val b = (color.blue * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    return Color(r, g, b, color.alpha)
}

// Helper function to create light color for icon container in Recent Calculations
fun lightenIconContainerColor(color: Color): Color {
    // Mix with white (70% white, 30% original color) to create a light tint
    val lightenFactor = 0.7f
    val r = (color.red * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    val g = (color.green * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    val b = (color.blue * (1 - lightenFactor) + lightenFactor).coerceIn(0f, 1f)
    return Color(r, g, b, color.alpha)
}

// Helper function to create dark color for icons in Recent Calculations
fun darkenIconColor(color: Color): Color {
    // Darken by reducing brightness (multiply by 0.6)
    val darkenFactor = 0.6f
    val r = (color.red * darkenFactor).coerceIn(0f, 1f)
    val g = (color.green * darkenFactor).coerceIn(0f, 1f)
    val b = (color.blue * darkenFactor).coerceIn(0f, 1f)
    return Color(r, g, b, color.alpha)
}

// Get icon resource for Recent Calculations (using category icons)
fun getIconResourceForRecentCalculation(iconName: String): Int {
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


package com.belbytes.calculators.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belbytes.calculators.utils.PreferenceManager

@Composable
fun DecimalPlacesScreen(
    onDecimalPlacesSelected: (Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedPlaces by remember { 
        mutableStateOf(PreferenceManager.getDecimalPlaces(context))
    }
    
    val decimalOptions = listOf(0, 1, 2, 3, 4, 5)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        HeaderSection(
            title = "Select Decimal Places",
            onBackClick = { /* Handle back */ },
            onDoneClick = {
                PreferenceManager.setDecimalPlaces(context, selectedPlaces)
                onDecimalPlacesSelected(selectedPlaces)
            }
        )
        
        // Decimal Options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            decimalOptions.forEach { places ->
                DecimalPlacesOption(
                    places = places,
                    example = formatExample(places),
                    isSelected = selectedPlaces == places,
                    onClick = { selectedPlaces = places }
                )
            }
        }
    }
}

@Composable
fun DecimalPlacesOption(
    places: Int,
    example: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color(0xFF2196F3),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = places.toString(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Example
                Text(
                    text = example,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }
            
            // Radio Button
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (isSelected) Color(0xFF4CAF50) else Color.White,
                        shape = CircleShape
                    )
                    .border(
                        width = if (isSelected) 0.dp else 2.dp,
                        color = Color(0xFFE0E0E0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatExample(places: Int): String {
    val base = "123"
    return if (places == 0) {
        base
    } else {
        val decimals = "456789".take(places)
        "$base.$decimals"
    }
}


package com.belbytes.calculators.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.belbytes.calculators.R
import com.belbytes.calculators.utils.PreferenceManager

data class NumberFormatOption(
    val label: String,
    val format: String,
    val example: String
)

@Composable
fun NumberFormatScreen(
    onFormatSelected: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val formats = listOf(
        NumberFormatOption(context.getString(R.string.format_automatic), "auto", context.getString(R.string.format_automatic)),
        NumberFormatOption(context.getString(R.string.format_indian), "12,34,567.89", context.getString(R.string.format_indian)),
        NumberFormatOption(context.getString(R.string.format_european_space), "1 234 567,89", context.getString(R.string.format_european_space)),
        NumberFormatOption(context.getString(R.string.format_swiss), "1'234'567.89", context.getString(R.string.format_swiss)),
        NumberFormatOption(context.getString(R.string.format_european_dot), "1.234.567,89", context.getString(R.string.format_european_dot)),
        NumberFormatOption(context.getString(R.string.format_us_uk), "1,234,567.89", context.getString(R.string.format_us_uk))
    )
    
    var selectedFormat by remember { 
        mutableStateOf(PreferenceManager.getNumberFormat(context))
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        HeaderSection(
            title = context.getString(R.string.select_number_format),
            onBackClick = { /* Handle back */ },
            onDoneClick = {
                PreferenceManager.setNumberFormat(context, selectedFormat)
                onFormatSelected(selectedFormat)
            }
        )
        
        // Format Options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            formats.forEach { formatOption ->
                NumberFormatOption(
                    format = formatOption.format,
                    label = formatOption.label,
                    example = formatOption.example,
                    isSelected = selectedFormat == formatOption.format,
                    onClick = { selectedFormat = formatOption.format }
                )
            }
        }
    }
}

@Composable
fun NumberFormatOption(
    format: String,
    label: String,
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
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            
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


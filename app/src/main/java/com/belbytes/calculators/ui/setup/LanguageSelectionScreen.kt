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
import com.belbytes.calculators.utils.PreferenceManager

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedLanguage by remember { 
        mutableStateOf(PreferenceManager.getSelectedLanguage(context))
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        HeaderSection(
            title = "Language",
            onBackClick = { /* Can't go back from first screen */ },
            onDoneClick = {
                PreferenceManager.setSelectedLanguage(context, selectedLanguage)
                onLanguageSelected(selectedLanguage)
            }
        )
        
        // Language Options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LanguageOption(
                language = "English",
                languageCode = "en",
                isSelected = selectedLanguage == "en",
                onClick = { selectedLanguage = "en" }
            )
            
            LanguageOption(
                language = "हिंदी",
                languageCode = "hi",
                isSelected = selectedLanguage == "hi",
                onClick = { selectedLanguage = "hi" }
            )
        }
    }
}

@Composable
fun LanguageOption(
    language: String,
    languageCode: String,
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
                text = language,
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



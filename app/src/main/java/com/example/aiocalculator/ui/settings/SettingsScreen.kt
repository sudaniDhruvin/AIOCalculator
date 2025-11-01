package com.example.aiocalculator.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SettingsItem(
    val id: String,
    val title: String,
    val iconName: String,
    val color: String,
    val onClick: () -> Unit
)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {}
) {
    val settingsItems = listOf(
        SettingsItem(
            id = "1",
            title = "Share App",
            iconName = "share",
            color = "#4CAF50", // Light green
            onClick = { /* Handle share app */ }
        ),
        SettingsItem(
            id = "2",
            title = "Rate App",
            iconName = "rate",
            color = "#2196F3", // Light blue
            onClick = { /* Handle rate app */ }
        ),
        SettingsItem(
            id = "3",
            title = "Privacy Policy",
            iconName = "privacy",
            color = "#795548", // Brown
            onClick = { /* Handle privacy policy */ }
        ),
        SettingsItem(
            id = "4",
            title = "More App",
            iconName = "more",
            color = "#FF9800", // Orange
            onClick = { /* Handle more app */ }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Blue Header
        HeaderSection()
        
        // Settings Items List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            settingsItems.forEach { item ->
                SettingsItemCard(
                    item = item,
                    onClick = item.onClick
                )
            }
        }
        
        // Bottom padding for navigation bar
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color(0xFF2196F3)) // Blue color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingsItemCard(
    item: SettingsItem,
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
            // Icon on the left
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(item.color)),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForSettingsItem(item.iconName),
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text in center
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            // Chevron arrow on the right
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Navigate",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun getIconForSettingsItem(iconName: String): ImageVector {
    return when (iconName) {
        "share" -> Icons.Default.Share // Share icon
        "rate" -> Icons.Default.Star // Star icon for rate
        "privacy" -> Icons.Default.Lock // Lock/Shield icon for privacy
        "more" -> Icons.Default.Home // Grid icon for more
        else -> Icons.Default.Settings
    }
}

package com.belbytes.calculators.ui.navigation

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.belbytes.calculators.data.BottomNavItem
import com.belbytes.calculators.data.DataRepository
import com.belbytes.calculators.ui.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    val context = LocalContext.current
    var navItems by remember { mutableStateOf<List<BottomNavItem>>(emptyList()) }

    // Load navigation items once
    LaunchedEffect(Unit) {
        val data = DataRepository.loadAppData(context)
        navItems = data.bottomNavigationItems
    }

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 4.dp
    ) {
        navItems.forEach { item ->
            val isSelected = item.route == currentRoute
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = getIconForNavItem(item.iconName),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        // Navigate to the selected tab with proper back stack handling
                        navController.navigate(item.route) {
                            // Pop up to start destination (home) when navigating to tabs
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2196F3), // Blue for selected
                    selectedTextColor = Color(0xFF2196F3), // Blue for selected text
                    indicatorColor = Color.Transparent, // No indicator background
                    unselectedIconColor = Color(0xFF757575), // Gray for unselected
                    unselectedTextColor = Color(0xFF757575) // Gray for unselected text
                )
            )
        }
    }
}

@Composable
fun getIconForNavItem(iconName: String): ImageVector {
    return when (iconName) {
        "ic_nav_home" -> Icons.Default.Home
        "ic_nav_calculators" -> Icons.Default.Add // Grid icon for calculators
        "ic_nav_history" -> Icons.Default.ShoppingCart // Clock with arrow for history
        "ic_nav_settings" -> Icons.Default.Settings
        else -> Icons.Default.Home
    }
}


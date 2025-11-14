package com.belbytes.calculators.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.belbytes.calculators.R

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
    val context = LocalContext.current
    val packageName = context.packageName
    
    val settingsItems = listOf(
        SettingsItem(
            id = "1",
            title = "Share App",
            iconName = "share",
            color = "#E8F5E9", // Very light green
            onClick = { shareApp(context, packageName) }
        ),
        SettingsItem(
            id = "2",
            title = "Rate App",
            iconName = "rate",
            color = "#E3F2FD", // Very light blue
            onClick = { rateApp(context, packageName) }
        ),
        SettingsItem(
            id = "3",
            title = "Privacy Policy",
            iconName = "privacy",
            color = "#EFEBE9", // Very light brown/maroon
            onClick = { openPrivacyPolicy(context) }
        ),
        SettingsItem(
            id = "4",
            title = "More App",
            iconName = "more",
            color = "#FFF3E0", // Very light orange
            onClick = { moreApps(context) }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)) // Off-white background
    ) {
        // Blue Header
        HeaderSection()
        
        // Settings Items List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing between cards
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
            .height(110.dp)
            .background(Color(0xFF2196F3)) // Blue color
            .statusBarsPadding()
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Subtle shadow
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon on the left - rounded square with light color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(item.color)),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getIconResourceForSettingsItem(item.iconName)),
                    contentDescription = item.title,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text in center
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            // Chevron arrow on the right
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color.Black,
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

fun getIconResourceForSettingsItem(iconName: String): Int {
    return when (iconName) {
        "share" -> R.drawable.share
        "rate" -> R.drawable.rate
        "privacy" -> R.drawable.privacy
        "more" -> R.drawable.more
        else -> R.drawable.settings
    }
}

/**
 * Share the app with friends
 */
private fun shareApp(context: android.content.Context, packageName: String) {
    val shareMessage = "Check out this amazing app: https://play.google.com/store/apps/details?id=$packageName"
    
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
        putExtra(Intent.EXTRA_TEXT, shareMessage)
    }
    
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

/**
 * Open the app's rating page on Google Play Store
 */
private fun rateApp(context: android.content.Context, packageName: String) {
    try {
        // Open Google Play Store app if installed
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Open in web browser if Play Store app is not installed
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

/**
 * Open privacy policy URL in browser
 */
private fun openPrivacyPolicy(context: android.content.Context) {
    val privacyUrl = "https://belcode.blogspot.com/2024/12/privacy-policy-emi-loan-calculator-app.html"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

/**
 * Open developer's apps page on Google Play Store
 */
private fun moreApps(context: android.content.Context) {
    try {
        // Open Play Store to developer's apps page
        val uri = Uri.parse("market://search?q=pub:BelBytes")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Open in browser if Play Store is not installed
        val uri = Uri.parse("https://play.google.com/store/apps/developer?id=BelBytes")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

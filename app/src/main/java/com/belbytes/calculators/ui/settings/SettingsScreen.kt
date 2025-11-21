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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.belbytes.calculators.R
import com.belbytes.calculators.utils.PreferenceManager
import com.belbytes.calculators.utils.LocaleHelper
import com.belbytes.calculators.ui.setup.*

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
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showNumberFormatDialog by remember { mutableStateOf(false) }
    var showDecimalPlacesDialog by remember { mutableStateOf(false) }
    
    val settingsItems = listOf(
        SettingsItem(
            id = "language",
            title = "Language",
            iconName = "language",
            color = "#E1F5FE", // Very light blue
            onClick = { showLanguageDialog = true }
        ),
        SettingsItem(
            id = "number_format",
            title = "Number Format",
            iconName = "format",
            color = "#F3E5F5", // Very light purple
            onClick = { showNumberFormatDialog = true }
        ),
        SettingsItem(
            id = "decimal_places",
            title = "Decimal Places",
            iconName = "decimal",
            color = "#FFF9C4", // Very light yellow
            onClick = { showDecimalPlacesDialog = true }
        ),
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
                    onClick = item.onClick,
                    subtitle = when (item.id) {
                        "language" -> {
                            val lang = PreferenceManager.getSelectedLanguage(context)
                            if (lang == "hi") "हिंदी" else "English"
                        }
                        "number_format" -> {
                            PreferenceManager.getNumberFormat(context)
                        }
                        "decimal_places" -> {
                            "${PreferenceManager.getDecimalPlaces(context)} places"
                        }
                        else -> null
                    }
                )
            }
        }
        
        // Bottom padding for navigation bar
        Spacer(modifier = Modifier.height(80.dp))
    }
    
    // Language Selection Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { language ->
                PreferenceManager.setSelectedLanguage(context, language)
                LocaleHelper.setLocale(context, language)
                showLanguageDialog = false
            }
        )
    }
    
    // Number Format Selection Dialog
    if (showNumberFormatDialog) {
        NumberFormatSelectionDialog(
            onDismiss = { showNumberFormatDialog = false },
            onFormatSelected = { format ->
                PreferenceManager.setNumberFormat(context, format)
                showNumberFormatDialog = false
            }
        )
    }
    
    // Decimal Places Selection Dialog
    if (showDecimalPlacesDialog) {
        DecimalPlacesSelectionDialog(
            onDismiss = { showDecimalPlacesDialog = false },
            onDecimalPlacesSelected = { places ->
                PreferenceManager.setDecimalPlaces(context, places)
                showDecimalPlacesDialog = false
            }
        )
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
    onClick: () -> Unit,
    subtitle: String? = null
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
                when (item.iconName) {
                    "language" -> Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = item.title,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(24.dp)
                    )
                    "format" -> Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = item.title,
                        tint = Color(0xFF9C27B0),
                        modifier = Modifier.size(24.dp)
                    )
                    "decimal" -> Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = item.title,
                        tint = Color(0xFFF57F17),
                        modifier = Modifier.size(24.dp)
                    )
                    else -> Image(
                        painter = painterResource(id = getIconResourceForSettingsItem(item.iconName)),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text in center
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
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

@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedLanguage by remember { 
        mutableStateOf(PreferenceManager.getSelectedLanguage(context))
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Language",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Column(
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
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            PreferenceManager.setSelectedLanguage(context, selectedLanguage)
                            onLanguageSelected(selectedLanguage)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun NumberFormatSelectionDialog(
    onDismiss: () -> Unit,
    onFormatSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val formats = listOf(
        NumberFormatOption("Automatic", "auto", "Auto"),
        NumberFormatOption("12,34,567.89", "12,34,567.89", "12,34,567.89"),
        NumberFormatOption("1 234 567,89", "1 234 567,89", "1 234 567,89"),
        NumberFormatOption("1'234'567.89", "1'234'567.89", "1'234'567.89"),
        NumberFormatOption("1.234.567,89", "1.234.567,89", "1.234.567,89"),
        NumberFormatOption("1,234,567.89", "1,234,567.89", "1,234,567.89")
    )
    
    var selectedFormat by remember { 
        mutableStateOf(PreferenceManager.getNumberFormat(context))
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 500.dp)
            ) {
                Text(
                    text = "Select Number Format",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
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
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            PreferenceManager.setNumberFormat(context, selectedFormat)
                            onFormatSelected(selectedFormat)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun DecimalPlacesSelectionDialog(
    onDismiss: () -> Unit,
    onDecimalPlacesSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    var selectedPlaces by remember { 
        mutableStateOf(PreferenceManager.getDecimalPlaces(context))
    }
    
    val decimalOptions = listOf(0, 1, 2, 3, 4, 5)
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Decimal Places",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Column(
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
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            PreferenceManager.setDecimalPlaces(context, selectedPlaces)
                            onDecimalPlacesSelected(selectedPlaces)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("Save")
                    }
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

package com.belbytes.calculators

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.belbytes.calculators.ui.navigation.BottomNavigationBar
import com.belbytes.calculators.ui.navigation.NavigationGraph
import com.belbytes.calculators.ui.navigation.Screen
import com.belbytes.calculators.ui.theme.AIOCalculatorTheme
import com.belbytes.calculators.utils.LocaleHelper
import com.belbytes.calculators.utils.PreferenceManager
import java.util.*

class MainActivity : ComponentActivity() {
    private var currentLanguage: String = ""
    
    override fun attachBaseContext(newBase: Context?) {
        val languageCode = PreferenceManager.getSelectedLanguage(newBase ?: return)
        val locale = Locale(languageCode)
        val config = Configuration()
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Track current language
        currentLanguage = PreferenceManager.getSelectedLanguage(this)
        
        // Update language based on user preference
        LocaleHelper.updateLanguage(this)
        
        enableEdgeToEdge()
        setContent {
            AIOCalculatorTheme {
                MainScreen()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Check if language has changed and recreate if needed
        val newLanguage = PreferenceManager.getSelectedLanguage(this)
        if (currentLanguage != newLanguage) {
            currentLanguage = newLanguage
            recreate()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom navigation only for main screens
            if (currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Calculators.route,
                    Screen.History.route,
                    Screen.Settings.route
                )
            ) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            startDestination = Screen.Home.route
        )
    }
}
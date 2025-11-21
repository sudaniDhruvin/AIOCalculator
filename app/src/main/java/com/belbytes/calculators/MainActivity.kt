package com.belbytes.calculators

import android.content.Context
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Update language based on user preference
        LocaleHelper.updateLanguage(this)
        
        enableEdgeToEdge()
        setContent {
            AIOCalculatorTheme {
                MainScreen()
            }
        }
    }
    
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(
            newBase?.let { LocaleHelper.setLocale(it, PreferenceManager.getSelectedLanguage(it)) }
        )
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
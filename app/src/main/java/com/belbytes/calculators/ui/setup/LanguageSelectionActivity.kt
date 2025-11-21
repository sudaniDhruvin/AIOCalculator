package com.belbytes.calculators.ui.setup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.belbytes.calculators.ui.onboarding.OnboardingActivity
import com.belbytes.calculators.ui.theme.AIOCalculatorTheme
import com.belbytes.calculators.utils.LocaleHelper
import com.belbytes.calculators.utils.PreferenceManager

class LanguageSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIOCalculatorTheme {
                LanguageSelectionScreen(
                    onLanguageSelected = { language ->
                        PreferenceManager.setSelectedLanguage(this, language)
                        // Update app language immediately
                        LocaleHelper.setLocale(this, language)
                        // Navigate to onboarding after language selection
                        startActivity(Intent(this, OnboardingActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}


package com.belbytes.calculators.ui.setup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.belbytes.calculators.MainActivity
import com.belbytes.calculators.ui.theme.AIOCalculatorTheme
import com.belbytes.calculators.utils.PreferenceManager

class SetupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIOCalculatorTheme {
                SetupFlowScreen(
                    onComplete = {
                        PreferenceManager.setFirstTimeLaunch(this, false)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SetupFlowScreen(onComplete: () -> Unit) {
    var currentStep by remember { mutableStateOf(0) }
    
    when (currentStep) {
        0 -> NumberFormatScreen(
            onFormatSelected = { currentStep = 1 }
        )
        1 -> DecimalPlacesScreen(
            onDecimalPlacesSelected = { onComplete() }
        )
    }
}

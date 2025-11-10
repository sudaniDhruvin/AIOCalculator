package com.belbytes.calculators

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.belbytes.calculators.ui.theme.AIOCalculatorTheme
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    private val AD_UNIT_ID = "ca-app-pub-2192933586407526/1751383170"
    private var appOpenAd: AppOpenAd? = null
    private var isFirstTime: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AdMob
        MobileAds.initialize(this) {}
        
        // Retrieve SharedPreferences
        val prefs: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        isFirstTime = prefs.getBoolean("FirstTimeLaunch", true)
        
        // Show custom Compose splash screen immediately
        setContent {
            AIOCalculatorTheme {
                SplashScreenContent()
            }
        }
        
        // Load and show App Open Ad
        loadAndShowAppOpenAd()
    }
    
    private fun goToActivity(isFirstTime: Boolean) {
        lifecycleScope.launch {
            delay(10) // Small delay
            if (!isFirstTime) {
                goToMainActivity()
            } else {
                goToMainActivity()
            }
        }
    }
    
    private fun goToMainActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
    
    private fun loadAndShowAppOpenAd() {
        val adRequest = AdRequest.Builder().build()
        
        AppOpenAd.load(
            this,
            AD_UNIT_ID,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    
                    // Set FullScreenContentCallback
                    appOpenAd?.setFullScreenContentCallback(object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            // Ad is shown
                        }
                        
                        override fun onAdDismissedFullScreenContent() {
                            // Ad is closed by the user
                            appOpenAd = null
                            goToActivity(isFirstTime)
                        }
                        
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Ad failed to show
                            appOpenAd = null
                            goToActivity(isFirstTime)
                        }
                    })
                    
                    // Show the ad
                    appOpenAd?.show(this@SplashActivity)
                }
                
                override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                    // Ad failed to load, continue to next screen
                    appOpenAd = null
                    goToActivity(isFirstTime)
                }
            }
        )
    }
}

@Composable
fun SplashScreenContent() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = "Splash Logo",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}


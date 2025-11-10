package com.belbytes.calculators.ads

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.belbytes.calculators.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

/**
 * Compose component for displaying a Banner Ad
 */
@Composable
fun BannerAd(
    adUnitId: String = "ca-app-pub-2192933586407526/3914512081",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        MobileAds.initialize(context) {}
    }
    
    AndroidView(
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.LARGE_BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = modifier
    )
}

/**
 * Compose component for displaying a Native Ad with shimmer loading effect
 */
@Composable
fun NativeAd(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-2192933586407526/3387898470"
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        MobileAds.initialize(context) {}
    }
    
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                val container = FrameLayout(ctx)
                val shimmer = LayoutInflater.from(ctx)
                    .inflate(R.layout.native_ad_shimmer, container, false) as ShimmerFrameLayout
                
                container.addView(shimmer)
                
                // Load native ad
                NativeAdHelper.loadNativeAd(ctx, container, shimmer)
                
                container
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


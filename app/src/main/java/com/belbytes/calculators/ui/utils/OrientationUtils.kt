package com.belbytes.calculators.ui.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Composable that sets the screen orientation to landscape when entered
 * and restores it to portrait when exited
 */
@Composable
fun LockScreenOrientationLandscape() {
    val context = LocalContext.current
    val activity = context as? Activity
    
    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation
        // Set to landscape
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        onDispose {
            // Restore to portrait when leaving the screen
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}


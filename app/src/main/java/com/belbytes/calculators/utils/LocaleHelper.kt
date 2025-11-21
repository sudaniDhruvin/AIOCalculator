package com.belbytes.calculators.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

object LocaleHelper {
    
    /**
     * Set the app locale based on selected language
     */
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        
        return context.createConfigurationContext(configuration)
    }
    
    /**
     * Get the current locale
     */
    fun getLocale(context: Context): Locale {
        val languageCode = PreferenceManager.getSelectedLanguage(context)
        return Locale(languageCode)
    }
    
    /**
     * Update app language based on preference
     * Call this in MainActivity onCreate or when language changes
     */
    fun updateLanguage(context: Context) {
        val languageCode = PreferenceManager.getSelectedLanguage(context)
        setLocale(context, languageCode)
    }
}


package com.belbytes.calculators.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "MyAppPrefs"
    
    // Keys
    private const val KEY_FIRST_TIME_LAUNCH = "FirstTimeLaunch"
    private const val KEY_ONBOARDING_COMPLETED = "OnboardingCompleted"
    private const val KEY_SELECTED_LANGUAGE = "SelectedLanguage"
    private const val KEY_NUMBER_FORMAT = "NumberFormat"
    private const val KEY_DECIMAL_PLACES = "DecimalPlaces"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // First Time Launch
    fun isFirstTimeLaunch(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_FIRST_TIME_LAUNCH, true)
    }
    
    fun setFirstTimeLaunch(context: Context, value: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_FIRST_TIME_LAUNCH, value).apply()
    }
    
    // Onboarding Completed
    fun isOnboardingCompleted(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    fun setOnboardingCompleted(context: Context, value: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()
    }
    
    // Language Selection
    fun getSelectedLanguage(context: Context): String {
        return getSharedPreferences(context).getString(KEY_SELECTED_LANGUAGE, "en") ?: "en"
    }
    
    fun setSelectedLanguage(context: Context, language: String) {
        getSharedPreferences(context).edit().putString(KEY_SELECTED_LANGUAGE, language).apply()
    }
    
    // Number Format
    fun getNumberFormat(context: Context): String {
        return getSharedPreferences(context).getString(KEY_NUMBER_FORMAT, "12,34,567.89") ?: "12,34,567.89"
    }
    
    fun setNumberFormat(context: Context, format: String) {
        getSharedPreferences(context).edit().putString(KEY_NUMBER_FORMAT, format).apply()
    }
    
    // Decimal Places
    fun getDecimalPlaces(context: Context): Int {
        return getSharedPreferences(context).getInt(KEY_DECIMAL_PLACES, 0)
    }
    
    fun setDecimalPlaces(context: Context, places: Int) {
        getSharedPreferences(context).edit().putInt(KEY_DECIMAL_PLACES, places).apply()
    }
}


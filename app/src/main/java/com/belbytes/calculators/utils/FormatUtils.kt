package com.belbytes.calculators.utils

import android.content.Context

/**
 * Utility functions for formatting numbers and currency
 * These functions use the user's preferences for number format and decimal places
 */

/**
 * Format currency with decimal places (typically used for money values)
 * Uses user's preferred number format and decimal places
 */
fun formatCurrencyWithDecimal(context: Context, amount: Double): String {
    return NumberFormatter.formatCurrency(context, amount)
}

/**
 * Format number with decimal places
 * Uses user's preferred number format and decimal places
 */
fun formatNumber(context: Context, value: Double): String {
    return NumberFormatter.formatNumber(context, value)
}

/**
 * Format number - if whole number, show as integer, otherwise with decimal
 * Uses user's preferred number format and decimal places
 */
fun formatNumberWithDecimal(context: Context, value: Double): String {
    val decimalPlaces = PreferenceManager.getDecimalPlaces(context)
    // If value is whole number and user wants 0 decimal places, show as integer
    if (value % 1.0 == 0.0 && decimalPlaces == 0) {
        return NumberFormatter.formatNumber(context, value)
    }
    return NumberFormatter.formatNumber(context, value)
}

/**
 * Parse a formatted number string back to Double
 * Handles different number formats based on user preference
 */
fun parseFormattedNumber(context: Context, formattedValue: String): Double? {
    return NumberFormatter.parseNumber(context, formattedValue)
}


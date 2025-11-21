package com.belbytes.calculators.utils

import android.content.Context
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object NumberFormatter {
    
    /**
     * Format a number based on user preferences (format and decimal places)
     */
    fun formatNumber(context: Context, value: Double): String {
        val format = PreferenceManager.getNumberFormat(context)
        val decimalPlaces = PreferenceManager.getDecimalPlaces(context)
        
        return formatNumberWithFormat(value, format, decimalPlaces)
    }
    
    /**
     * Format a currency amount (typically with 2 decimal places by default, but respects user preference)
     */
    fun formatCurrency(context: Context, value: Double): String {
        val format = PreferenceManager.getNumberFormat(context)
        val decimalPlaces = PreferenceManager.getDecimalPlaces(context)
        // For currency, use at least 2 decimal places if user hasn't set preference, otherwise use user preference
        val places = if (decimalPlaces == 0 && PreferenceManager.isFirstTimeLaunch(context)) 2 else decimalPlaces
        
        return formatNumberWithFormat(value, format, places)
    }
    
    /**
     * Format number with specific format and decimal places
     */
    private fun formatNumberWithFormat(value: Double, format: String, decimalPlaces: Int): String {
        // First format the number with the required decimal places
        val decimalFormat = DecimalFormat("#." + "0".repeat(decimalPlaces))
        val formattedValue = decimalFormat.format(value)
        
        // Split into integer and decimal parts
        val parts = formattedValue.split(".")
        var integerPart = parts[0]
        val decimalPart = if (parts.size > 1 && decimalPlaces > 0) parts[1] else ""
        
        // Apply number format based on selection
        when (format) {
            "auto" -> {
                // Use system locale default
                return if (decimalPart.isNotEmpty()) "$integerPart.$decimalPart" else integerPart
            }
            "12,34,567.89" -> {
                // Indian numbering system (Lakhs/Crores)
                integerPart = formatIndianNumber(integerPart)
                return if (decimalPart.isNotEmpty()) "$integerPart.$decimalPart" else integerPart
            }
            "1 234 567,89" -> {
                // European format with space separator and comma decimal
                integerPart = formatWithSeparator(integerPart, " ")
                return if (decimalPart.isNotEmpty()) "$integerPart,$decimalPart" else integerPart
            }
            "1'234'567.89" -> {
                // Swiss format with apostrophe separator
                integerPart = formatWithSeparator(integerPart, "'")
                return if (decimalPart.isNotEmpty()) "$integerPart.$decimalPart" else integerPart
            }
            "1.234.567,89" -> {
                // European format with dot separator and comma decimal
                integerPart = formatWithSeparator(integerPart, ".")
                return if (decimalPart.isNotEmpty()) "$integerPart,$decimalPart" else integerPart
            }
            "1,234,567.89" -> {
                // US/UK format with comma separator
                integerPart = formatWithSeparator(integerPart, ",")
                return if (decimalPart.isNotEmpty()) "$integerPart.$decimalPart" else integerPart
            }
            else -> {
                // Default to Indian format
                integerPart = formatIndianNumber(integerPart)
                return if (decimalPart.isNotEmpty()) "$integerPart.$decimalPart" else integerPart
            }
        }
    }
    
    /**
     * Format number in Indian numbering system (Lakhs/Crores)
     * Example: 1234567 -> 12,34,567
     * Pattern: First 3 digits from right, then every 2 digits
     */
    private fun formatIndianNumber(number: String): String {
        if (number.isEmpty()) return number
        
        val isNegative = number.startsWith("-")
        val num = if (isNegative) number.substring(1) else number
        
        if (num.length <= 3) {
            return if (isNegative) "-$num" else num
        }
        
        val result = StringBuilder()
        var digitCount = 0
        
        // Process from right to left
        for (i in num.length - 1 downTo 0) {
            // After first 3 digits, add comma every 2 digits
            if (digitCount == 3) {
                result.insert(0, ',')
            } else if (digitCount > 3 && (digitCount - 3) % 2 == 0) {
                result.insert(0, ',')
            }
            result.insert(0, num[i])
            digitCount++
        }
        
        return if (isNegative) "-$result" else result.toString()
    }
    
    /**
     * Format number with a specific separator (for thousands)
     */
    private fun formatWithSeparator(number: String, separator: String): String {
        if (number.isEmpty()) return number
        
        val isNegative = number.startsWith("-")
        val num = if (isNegative) number.substring(1) else number
        
        if (num.length <= 3) {
            return if (isNegative) "-$num" else num
        }
        
        val result = StringBuilder()
        var count = 0
        
        // Process from right to left, add separator every 3 digits
        for (i in num.length - 1 downTo 0) {
            if (count > 0 && count % 3 == 0) {
                result.insert(0, separator)
            }
            result.insert(0, num[i])
            count++
        }
        
        return if (isNegative) "-$result" else result.toString()
    }
    
    /**
     * Parse a formatted number string back to Double
     * Handles different number formats
     */
    fun parseNumber(context: Context, formattedValue: String): Double? {
        val format = PreferenceManager.getNumberFormat(context)
        
        // Remove all separators and normalize decimal separator
        var cleaned = formattedValue.trim()
        
        // Handle negative sign
        val isNegative = cleaned.startsWith("-")
        if (isNegative) cleaned = cleaned.substring(1)
        
        when (format) {
            "1 234 567,89", "1.234.567,89" -> {
                // European format: comma is decimal separator
                cleaned = cleaned.replace(" ", "").replace(".", "").replace(",", ".")
            }
            "12,34,567.89", "1'234'567.89", "1,234,567.89" -> {
                // Dot is decimal separator, remove other separators
                cleaned = cleaned.replace(",", "").replace("'", "").replace(" ", "")
            }
            else -> {
                // Default: try to detect
                if (cleaned.contains(",") && cleaned.contains(".")) {
                    // Has both, assume last one is decimal
                    val lastComma = cleaned.lastIndexOf(",")
                    val lastDot = cleaned.lastIndexOf(".")
                    if (lastComma > lastDot) {
                        // Comma is decimal
                        cleaned = cleaned.replace(".", "").replace(",", ".")
                    } else {
                        // Dot is decimal
                        cleaned = cleaned.replace(",", "")
                    }
                } else if (cleaned.contains(",")) {
                    // Only comma - could be decimal or thousands separator
                    // Try to detect: if more than 3 digits after comma, it's thousands separator
                    val parts = cleaned.split(",")
                    if (parts.size > 1 && parts.last().length <= 2) {
                        // Likely decimal separator
                        cleaned = cleaned.replace(",", ".")
                    } else {
                        // Likely thousands separator
                        cleaned = cleaned.replace(",", "")
                    }
                } else {
                    // Remove spaces and apostrophes
                    cleaned = cleaned.replace(" ", "").replace("'", "")
                }
            }
        }
        
        return try {
            val value = cleaned.toDouble()
            if (isNegative) -value else value
        } catch (e: Exception) {
            null
        }
    }
}


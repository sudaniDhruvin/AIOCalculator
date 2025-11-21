package com.belbytes.calculators.utils

import android.content.Context
import com.belbytes.calculators.R

object LabelMapper {
    /**
     * Maps bottom navigation label to string resource ID
     */
    fun getBottomNavLabelResource(label: String): Int {
        return when (label.lowercase()) {
            "home" -> R.string.nav_home
            "calculators" -> R.string.nav_calculators
            "history" -> R.string.nav_history
            "settings" -> R.string.nav_settings
            else -> R.string.nav_home // fallback
        }
    }
    
    /**
     * Maps category name to string resource ID
     */
    fun getCategoryLabelResource(name: String): Int? {
        return when (name) {
            "EMI Calculators" -> R.string.category_emi_calculators
            "SIP Calculators" -> R.string.category_sip_calculators
            "Loan Calculators" -> R.string.category_loan_calculators
            "Bank Calculators" -> R.string.category_bank_calculators
            "GST & VAT" -> R.string.category_gst_vat
            "Other Calculators" -> R.string.category_other_calculators
            else -> null
        }
    }
    
    /**
     * Maps calculator subcategory name to string resource ID
     */
    fun getSubcategoryLabelResource(name: String): Int? {
        return when (name) {
            "EMI Calculator" -> R.string.subcategory_emi_calculator
            "Quick Calculator" -> R.string.subcategory_quick_calculator
            "Advance EMI" -> R.string.subcategory_advance_emi
            "Compare Loans" -> R.string.subcategory_compare_loans
            "SIP Calculator" -> R.string.subcategory_sip_calculator
            "Quick SIP" -> R.string.subcategory_quick_sip
            "Advance SIP" -> R.string.subcategory_advance_sip
            "Compare SIP" -> R.string.subcategory_compare_sip
            "SWP Calculator" -> R.string.subcategory_swp_calculator
            "STP Calculator" -> R.string.subcategory_stp_calculator
            "Pre Payment ROI Change" -> R.string.subcategory_pre_payment_roi_change
            "Check Eligibility" -> R.string.subcategory_check_eligibility
            "FD Calculator" -> R.string.subcategory_fd_calculator
            "RD Calculator" -> R.string.subcategory_rd_calculator
            "PPF Calculator" -> R.string.subcategory_ppf_calculator
            "Simple Interest" -> R.string.subcategory_simple_interest
            "GST Calculator" -> R.string.subcategory_gst_calculator
            "VAT Calculator" -> R.string.subcategory_vat_calculator
            "Discount Calculator" -> R.string.subcategory_discount_calculator
            "Cash Note Counter" -> R.string.subcategory_cash_note_counter
            "Charging Time" -> R.string.subcategory_charging_time
            else -> null
        }
    }
    
    /**
     * Gets localized label for bottom navigation
     */
    fun getLocalizedBottomNavLabel(context: Context, label: String): String {
        val resourceId = getBottomNavLabelResource(label)
        return context.getString(resourceId)
    }
    
    /**
     * Gets localized label for category
     */
    fun getLocalizedCategoryLabel(context: Context, name: String): String {
        val resourceId = getCategoryLabelResource(name)
        return if (resourceId != null) {
            context.getString(resourceId)
        } else {
            name // fallback to original name
        }
    }
    
    /**
     * Gets localized label for subcategory
     */
    fun getLocalizedSubcategoryLabel(context: Context, name: String): String {
        val resourceId = getSubcategoryLabelResource(name)
        return if (resourceId != null) {
            context.getString(resourceId)
        } else {
            name // fallback to original name
        }
    }
}


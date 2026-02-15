package com.lucdre.idleskills.skills.presentation.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Util object to format numbers.
 */
object NumberFormatter {
    private val numFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale.GERMANY)
        DecimalFormat("#,###", symbols)
    }

    fun formatNumber(number: Number): String {
        return numFormatter.format(number)
    }
}

/**
 * Extension function to format numbers.
 */
fun Number.formatNumber(): String {
    return NumberFormatter.formatNumber(this)
}

package com.lucdre.idleskills.ui.util

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

    /**
     * Formats a duration in milliseconds to a human-readable string.
     * Example: 3661000 -> "1h 1m 1s"
     */
    fun formatDuration(ms: Long): String {
        if (ms <= 0) return "N/A"
        val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(ms) % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}

/**
 * Extension function to format numbers.
 */
fun Number.formatNumber(): String {
    return NumberFormatter.formatNumber(this)
}

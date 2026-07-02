package com.lucdre.idleskills.profile.domain

import androidx.compose.runtime.Immutable

/**
 * User-specific settings and preferences.
 *
 * @property isNotificationsEnabled Whether notifications are active.
 * @property theme The selected UI theme.
 */
@Immutable
data class UserPreferences(
    val isNotificationsEnabled: Boolean = true,
    val theme: String = "SYSTEM"
)

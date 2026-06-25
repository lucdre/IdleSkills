package com.lucdre.idleskills.ui.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-device previews.
 */
@Preview(name = "Small Phone", widthDp = 320, showBackground = true, group = "Devices")
@Preview(name = "Medium Phone", device = Devices.PIXEL_4, showBackground = true, group = "Devices")
@Preview(name = "Large Phone", device = Devices.PIXEL_4_XL, showBackground = true, group = "Devices")
@Preview(name = "Tablet", device = Devices.PIXEL_TABLET, showBackground = true, group = "Devices")
//@Preview(name = "Dark Mode Small", widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, group = "Themes")
//@Preview(name = "Dark Mode Medium", device = Devices.PIXEL_4, uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, group = "Themes")
annotation class IdleSkillsPreviews

/**
 * Font scaling checks.
 */
@Preview(name = "Font 1.0x", fontScale = 1.0f, showBackground = true, group = "Font Scales")
@Preview(name = "Font 1.5x", fontScale = 1.5f, showBackground = true, group = "Font Scales")
@Preview(name = "Font 2.0x", fontScale = 2.0f, showBackground = true, group = "Font Scales")
annotation class FontScalePreviews

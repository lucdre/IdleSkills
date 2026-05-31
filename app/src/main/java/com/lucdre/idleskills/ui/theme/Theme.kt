package com.lucdre.idleskills.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.Black,

    secondary = SecondaryBlue,
    onSecondary = Color.White,

    background = Background,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,

    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,

    outline = Border,

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun IdleSkillsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
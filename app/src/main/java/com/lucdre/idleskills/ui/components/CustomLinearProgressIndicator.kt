package com.lucdre.idleskills.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A custom linear progress indicator. Doesn't clip at the end like the default one.
 *
 * @param modifier Modifier.
 * @param progressProvider The provider of the current progress, between 0 and 1.
 * @param progressColor The color of the progress bar.
 * @param backgroundColor The color of the background.
 * @param cornerRadius The radius [dp] of the corners.
 */
@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progressProvider: () -> Float,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    cornerRadius: Dp = 4.dp
) {
    Canvas(
        modifier = modifier.graphicsLayer()
    ) {
        val progress = progressProvider().coerceIn(0f, 1f)
        val radiusPx = cornerRadius.toPx()
        
        // Draw background track
        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )
        
        // Draw progress bar
        if (progress > 0f) {
            val progressWidth = size.width * progress
            drawRoundRect(
                color = progressColor,
                topLeft = Offset.Zero,
                size = Size(progressWidth, size.height),
                cornerRadius = CornerRadius(radiusPx, radiusPx)
            )
        }
    }
}

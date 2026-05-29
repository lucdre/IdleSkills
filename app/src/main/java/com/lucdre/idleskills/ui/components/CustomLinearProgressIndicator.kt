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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A custom linear progress indicator. Doesn't clip at the end like the default one.
 *
 * @param modifier Modifier.
 * @param progress The current progress, between 0 and 1.
 * @param progressColor The color of the progress bar.
 * @param backgroundColor The color of the background.
 * @param cornerRadius The radius [dp] of the corners.
 */
@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    cornerRadius: Dp = 4.dp
) {
    // Ensure progress is between 0 and 1
    val progressValue = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .semantics {
                this.progressBarRangeInfo = ProgressBarRangeInfo(
                    current = progressValue,
                    range = 0f..1f,
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawProgressBar(progressValue, progressColor)
        }
    }
}

private fun DrawScope.drawProgressBar(progress: Float, color: Color) {
    if (progress <= 0f) return
    
    val width = size.width * progress
    drawRect(
        color = color,
        topLeft = Offset.Zero,
        size = Size(width, size.height)
    )
}

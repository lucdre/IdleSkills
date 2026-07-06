package com.lucdre.idleskills.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp

/**
 * Text that scales to fit width.
 */
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
    minFontSize: TextUnit = 8.sp
) {
    // If fontSize is Unspecified, use the one from style
    val initialFontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier) {
        var scaledFontSize = initialFontSize
        if (scaledFontSize.isSp) {
            var currentSize = scaledFontSize.value
            var currentTextStyle = style.copy(fontSize = currentSize.sp)
            
            while (currentSize > minFontSize.value) {
                val measureResult = textMeasurer.measure(
                    text = text,
                    style = currentTextStyle,
                    maxLines = maxLines,
                    softWrap = softWrap,
                    overflow = overflow,
                    constraints = constraints
                )
                if (!measureResult.hasVisualOverflow) {
                    break
                }
                currentSize *= 0.9f
                currentTextStyle = currentTextStyle.copy(fontSize = currentSize.sp)
            }
            scaledFontSize = maxOf(currentSize, minFontSize.value).sp
        }

        Text(
            text = text,
            modifier = Modifier,
            color = color,
            fontSize = scaledFontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            style = style
        )
    }
}

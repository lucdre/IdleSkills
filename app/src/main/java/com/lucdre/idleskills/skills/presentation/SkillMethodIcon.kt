package com.lucdre.idleskills.skills.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.training.TrainingMethod

/**
 * A reusable composable for displaying a styled icon representing a skill training method.
 *
 * @param modifier Optional [Modifier] for this composable.
 * @param method The training method data to display.
 * @param isSelected Whether this method is currently selected.
 * @param onMethodSelected Callback invoked when this icon is clicked.
 * @param imageRes The drawable resource ID for the method's icon.
 * @param selectedBackgroundColor Background color when the method is selected.
 * @param unselectedBackgroundColor Background color when the method is not selected.
 * @param selectedIconTint Tint color for the icon when selected.
 * @param unselectedIconTint Tint color for the icon when not selected.
 * @param selectionIndicatorColor Color of the dot shown when selected.
 * @param iconSize The size of the main icon graphic. Default is 40.dp.
 * @param boxSize The total size of the clickable box. Default is 80.dp.
 */
@Composable
fun SkillMethodIcon(
    modifier: Modifier = Modifier,
    method: TrainingMethod,
    isSelected: Boolean,
    onMethodSelected: (TrainingMethod) -> Unit,
    @DrawableRes imageRes: Int,
    selectedBackgroundColor: Color,
    unselectedBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    selectedIconTint: Color,
    unselectedIconTint: Color,// = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    selectionIndicatorColor: Color,
    iconSize: Dp = 40.dp,
    boxSize: Dp = 80.dp
) {
    Box(
        modifier = modifier
            .size(boxSize)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) selectedBackgroundColor else unselectedBackgroundColor
            )
            .clickable { onMethodSelected(method) }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = painterResource(id = imageRes),
                contentDescription = method.name,
                tint = if (isSelected) selectedIconTint else unselectedIconTint
            )
            if (isSelected) {
                Box( // Selected indicator dot
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(8.dp)
                        .background(selectionIndicatorColor, CircleShape)
                )
            }
        }
    }
}

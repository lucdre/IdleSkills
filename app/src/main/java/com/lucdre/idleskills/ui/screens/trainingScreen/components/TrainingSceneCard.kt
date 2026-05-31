package com.lucdre.idleskills.ui.screens.trainingScreen.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.ui.components.CustomLinearProgressIndicator
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme

@Composable
fun TrainingSceneCard(
    modifier: Modifier = Modifier,
    regionName: String,
    activeSkill: SkillType?,
    methodName: String?,
    progressProvider: () -> Float,
    onRegionClick: () -> Unit
) {
    val theme = if (activeSkill != null) SkillMetadata.getTheme(activeSkill) else null
    val biomeColors = theme?.biomeColors ?: listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))

    // Smoothly interpolate progress updates (100ms logic updates -> 60fps visuals)
    // Snap to 0 immediately when resetting
    val animatedProgress by animateFloatAsState(
        targetValue = progressProvider(),
        animationSpec = if (progressProvider() == 0f) snap() else tween(durationMillis = 100, easing = LinearEasing),
        label = "trainingProgress"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(biomeColors))
        ) {
            // Region Selector
            Surface(
                onClick = onRegionClick,
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Forest, // Placeholder for region icon
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = regionName,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            // Area Info
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(12.dp)
            ) {
                Text(
                    text = activeSkill?.displayName ?: "Idle",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = methodName ?: "No active method",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Game Scene (Simplified representation of the character and object)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Character
                    AnimatedCharacter(activeSkill = activeSkill)
                    
                    Spacer(modifier = Modifier.width(40.dp))
                    
                    // Target Object (Tree/Rock)
                    TrainingTarget(activeSkill = activeSkill, progressProvider = { animatedProgress }, biome = biomeColors)
                }
            }
        }
    }
}

@Composable
fun TrainingTarget(activeSkill: SkillType?, progressProvider: () -> Float, biome: List<Color>) {
    Box(contentAlignment = Alignment.TopCenter) {
        Icon(
            imageVector = Icons.Default.Forest, // Placeholder
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )
        
        // Progress bar above the target
        if (activeSkill != null) {
            CustomLinearProgressIndicator(
                modifier = Modifier
                    .width(60.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                progress = progressProvider(),
                progressColor = biome[0].copy(alpha = 0.8f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedCharacter(activeSkill: SkillType?) {
    val infiniteTransition = rememberInfiniteTransition(label = "CharacterAnimation")

    val characterAnimationModifier = when (activeSkill) {
        SkillType.WOODCUTTING -> {
            val rotation by infiniteTransition.animateFloat(
                initialValue = -20f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "AxeSwing"
            )
            Modifier.graphicsLayer { rotationZ = rotation }
        }
        SkillType.MINING -> {
            val offset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "PickaxeHit"
            )
            Modifier.offset { IntOffset(0, offset.dp.roundToPx()) }
        }
        SkillType.FISHING -> {
            val bobbing by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "FishingBob"
            )
            Modifier.offset { IntOffset(0, bobbing.dp.roundToPx()) }
        }
        else -> Modifier
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = characterAnimationModifier
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Character",
            modifier = Modifier.size(80.dp),
            tint = Color.White
        )

        // Tool icon
        val toolIcon = when (activeSkill) {
            SkillType.WOODCUTTING -> Icons.Default.Handyman // Placeholder for Axe
            SkillType.MINING -> Icons.Default.Hardware // Placeholder for Pickaxe
            SkillType.FISHING -> Icons.Default.Phishing // Placeholder for Rod
            else -> null
        }

        toolIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(32.dp).offset(x = 20.dp, y = (-40).dp),
                tint = Color.LightGray
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TrainingSceneCardPreview() {
    IdleSkillsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TrainingSceneCard(
                regionName = "Region 1",
                activeSkill = SkillType.WOODCUTTING,
                methodName = "Oak Trees",
                progressProvider = { 0.45f },
                onRegionClick = {}
            )
        }
    }
}

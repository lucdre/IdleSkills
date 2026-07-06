package com.lucdre.idleskills.ui.screens.trainingScreen.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Handyman
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.lucdre.idleskills.R
import com.lucdre.idleskills.skills.domain.skill.SkillMetadata
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import com.lucdre.idleskills.ui.components.CustomLinearProgressIndicator
import com.lucdre.idleskills.ui.theme.IdleSkillsTheme
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TrainingSceneCard(
    modifier: Modifier = Modifier,
    regionName: String,
    activeSkill: SkillType?,
    methodType: TrainingMethodType?,
    startTime: Long,
    durationMs: Long,
    onRegionClick: () -> Unit
) {
    val theme = if (activeSkill != null) SkillMetadata.getTheme(activeSkill) else null
    val biomeColors = theme?.biomeColors ?: listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
    val biomeGradient = remember(biomeColors) { Brush.verticalGradient(biomeColors) }

    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(startTime, durationMs) {
        if (startTime == 0L || durationMs == 0L) {
            progress = 0f
        } else {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val newProgress = (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)
                progress = newProgress
                if (newProgress >= 1f) break
                delay(50.milliseconds)
            }
        }
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(biomeGradient)
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
                        imageVector = Icons.Default.Forest, // TODO: Region icon
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
                    text = methodType?.displayName ?: "No active method",
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
                    
                    // Target Object
                    TrainingTarget(activeSkill = activeSkill, progressProvider = { progress }, biome = biomeColors)
                }
            }
        }
    }
}

@Composable
fun TrainingTarget(activeSkill: SkillType?, progressProvider: () -> Float, biome: List<Color>) {
    Box(contentAlignment = Alignment.TopCenter) {
        Icon(
            imageVector = Icons.Default.Forest, // TODO: Target icon
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
                progressProvider = progressProvider,
                progressColor = biome[0].copy(alpha = 0.8f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedCharacter(activeSkill: SkillType?) {
    var animationFrame by remember(activeSkill) { mutableFloatStateOf(0f) }

    if (activeSkill != null) {
        LaunchedEffect(activeSkill) {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                animationFrame = elapsed.toFloat()
                delay(40.milliseconds) // 25 FPS animation ticks
            }
        }
    }

    val characterAnimationModifier = when (activeSkill) {
        SkillType.WOODCUTTING -> {
            Modifier.graphicsLayer {
                val elapsed = animationFrame
                val cycle = (elapsed % 1000) / 1000f
                val angle = if (cycle < 0.5f) {
                    -20f + (cycle * 2f) * 40f
                } else {
                    20f - ((cycle - 0.5f) * 2f) * 40f
                }
                rotationZ = angle
            }
        }
        SkillType.MINING -> {
            Modifier.graphicsLayer {
                val elapsed = animationFrame
                val cycle = (elapsed % 400) / 400f
                val offset = if (cycle < 0.5f) {
                    (cycle * 2f) * 10f
                } else {
                    10f - ((cycle - 0.5f) * 2f) * 10f
                }
                translationY = offset.dp.toPx()
            }
        }
        SkillType.FISHING -> {
            Modifier.graphicsLayer {
                val elapsed = animationFrame
                val cycle = (elapsed % 2000) / 2000f
                val bobbing = kotlin.math.sin(cycle * 2 * kotlin.math.PI.toFloat()) * 7.5f + 7.5f
                translationY = bobbing.dp.toPx()
            }
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
        val toolPainter = when (activeSkill) {
            SkillType.WOODCUTTING -> painterResource(id = R.drawable.skill_woodcutting_axe)
            SkillType.MINING -> painterResource(id = R.drawable.skill_mining_pickaxe)
            SkillType.FISHING -> painterResource(id = R.drawable.skill_fishing_rod)
            else -> null
        }

        toolPainter?.let {
            Icon(
                painter = it,
                contentDescription = null,
                modifier = Modifier.size(32.dp).offset(x = 20.dp, y = (-40).dp),
                tint = Color.Unspecified
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
                methodType = TrainingMethodType.WC_OAK,
                startTime = System.currentTimeMillis() - 2250,
                durationMs = 5000,
                onRegionClick = {}
            )
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.character.EmotionState
import com.example.ui.theme.Lavender
import com.example.ui.theme.PastelPink
import com.example.ui.theme.PrimaryPurple

@Composable
fun LunaAvatar(
    emotionState: EmotionState,
    size: Dp = 120.dp,
    isPlaying: Boolean = false,
    onTap: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "luna_motion")

    // Gentle breathing scale
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Musical head bounce when playing
    val musicBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) -4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "music_bounce"
    )

    // Glowing aura pulse
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    // Floating note animation
    val noteOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -16f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "note_float"
    )

    Box(
        modifier = modifier
            .size(size + 24.dp)
            .testTag("luna_avatar_container"),
        contentAlignment = Alignment.Center
    ) {
        // Magical Outer Glow Aura
        Box(
            modifier = Modifier
                .size(size + 16.dp)
                .scale(breathingScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Lavender.copy(alpha = auraAlpha * emotionState.auraIntensity),
                            PrimaryPurple.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Main Portrait Circle
        Box(
            modifier = Modifier
                .size(size)
                .scale(breathingScale)
                .offset(y = musicBounce.dp)
                .clip(CircleShape)
                .background(Color(0xFF211A46))
                .border(2.5.dp, Brush.sweepGradient(listOf(Lavender, PastelPink, PrimaryPurple, Lavender)), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap
                )
                .testTag("luna_avatar_image"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = emotionState.currentExpression.iconRes),
                contentDescription = emotionState.currentExpression.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Floating Musical Notes when playing
        if (isPlaying) {
            Text(
                text = "♪",
                color = PastelPink,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = noteOffset.dp)
            )
            Text(
                text = "♫",
                color = Lavender,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-4).dp, y = (noteOffset * 0.7f).dp)
            )
        }
    }
}

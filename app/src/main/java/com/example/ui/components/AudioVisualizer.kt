package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Lavender
import com.example.ui.theme.PastelPink
import com.example.ui.theme.PrimaryPurple

@Composable
fun AudioVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 28,
    maxHeight: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer_anim")

    val t1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (isPlaying) 0.95f else 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "t1"
    )

    val t2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = if (isPlaying) 0.85f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(580, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "t2"
    )

    val t3 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = if (isPlaying) 0.75f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "t3"
    )

    val gradient = Brush.verticalGradient(
        listOf(PastelPink, Lavender, PrimaryPurple)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight)
            .testTag("audio_visualizer"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val factor = when (i % 3) {
                0 -> t1
                1 -> t2
                else -> t3
            }
            // Curve the ends like a natural equalizer spectrum
            val posRatio = (i.toFloat() / (barCount - 1).toFloat())
            val bellCurve = 4f * posRatio * (1f - posRatio) // 0 at edges, 1 at center
            val dynamicHeight = (maxHeight.value * (0.15f + factor * 0.85f * (0.4f + bellCurve * 0.6f))).dp

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(dynamicHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(gradient)
            )
        }
    }
}

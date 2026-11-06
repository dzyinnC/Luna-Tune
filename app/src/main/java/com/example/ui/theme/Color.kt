package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// LunaTune Official Palette
val DeepNightPurple = Color(0xFF17132F)
val MidnightViolet = Color(0xFF211A46)
val PrimaryPurple = Color(0xFF7655D9)
val Lavender = Color(0xFFB9A3F4)
val SoftLavender = Color(0xFFE8DFFF)
val PastelPink = Color(0xFFF4C9E8)
val SoftWhite = Color(0xFFFAF8FF)
val MutedGray = Color(0xFFA8A3B8)

// Surface and Glass Elements
val DarkSurface = Color(0xFF1C1738)
val DarkSurfaceElevated = Color(0xFF282054)
val GlassBackground = Color(0x33282054)
val GlassBorder = Color(0x33B9A3F4)
val GlowPurple = Color(0x667655D9)
val GlowPink = Color(0x44F4C9E8)

// Gradients
val LunaGradient = Brush.verticalGradient(
    colors = listOf(MidnightViolet, DeepNightPurple)
)

val CardGlowGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF382B73), Color(0xFF251D4D))
)

val AuraGlowBrush = Brush.radialGradient(
    colors = listOf(Lavender.copy(alpha = 0.5f), PrimaryPurple.copy(alpha = 0.2f), Color.Transparent)
)

val ActiveTrackGradient = Brush.horizontalGradient(
    colors = listOf(PrimaryPurple, Lavender, PastelPink)
)

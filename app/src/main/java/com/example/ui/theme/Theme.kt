package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun getLunaColorScheme(wallpaperKey: String, songMood: String? = null): ColorScheme {
    return when (wallpaperKey.lowercase()) {
        "lavender_night" -> darkColorScheme(
            primary = Color(0xFF8B5CF6),
            onPrimary = SoftWhite,
            primaryContainer = Color(0xFF2E1A5E),
            onPrimaryContainer = Color(0xFFE9D5FF),
            secondary = Color(0xFFC4B5FD),
            onSecondary = Color(0xFF130E26),
            secondaryContainer = Color(0xFF1E1738),
            onSecondaryContainer = Color(0xFFE9D5FF),
            tertiary = Color(0xFFE9D5FF),
            onTertiary = Color(0xFF130E26),
            background = Color(0xFF130E26),
            onBackground = SoftWhite,
            surface = Color(0xFF1E1738),
            onSurface = SoftWhite,
            surfaceVariant = Color(0xFF2E2250),
            onSurfaceVariant = MutedGray,
            outline = Color(0x33C4B5FD)
        )

        "violet_galaxy" -> darkColorScheme(
            primary = Color(0xFF6366F1),
            onPrimary = SoftWhite,
            primaryContainer = Color(0xFF1E1B4B),
            onPrimaryContainer = Color(0xFFC7D2FE),
            secondary = Color(0xFFA5B4FC),
            onSecondary = Color(0xFF0F172A),
            secondaryContainer = Color(0xFF1E293B),
            onSecondaryContainer = Color(0xFFC7D2FE),
            tertiary = Color(0xFF818CF8),
            onTertiary = Color(0xFF0F172A),
            background = Color(0xFF0B0F19),
            onBackground = SoftWhite,
            surface = Color(0xFF131B2E),
            onSurface = SoftWhite,
            surfaceVariant = Color(0xFF1E293B),
            onSurfaceVariant = MutedGray,
            outline = Color(0x33818CF8)
        )

        "soft_pastel" -> darkColorScheme(
            primary = Color(0xFFD946EF),
            onPrimary = SoftWhite,
            primaryContainer = Color(0xFF4A154B),
            onPrimaryContainer = Color(0xFFFBCFE8),
            secondary = Color(0xFFF472B6),
            onSecondary = Color(0xFF201026),
            secondaryContainer = Color(0xFF2E1438),
            onSecondaryContainer = Color(0xFFFBCFE8),
            tertiary = Color(0xFFFBCFE8),
            onTertiary = Color(0xFF201026),
            background = Color(0xFF1A0B20),
            onBackground = SoftWhite,
            surface = Color(0xFF281232),
            onSurface = SoftWhite,
            surfaceVariant = Color(0xFF3B1B4A),
            onSurfaceVariant = MutedGray,
            outline = Color(0x33F472B6)
        )

        "moonlight" -> darkColorScheme(
            primary = Color(0xFF38BDF8),
            onPrimary = Color(0xFF0B132B),
            primaryContainer = Color(0xFF0C4A6E),
            onPrimaryContainer = Color(0xFFBAE6FD),
            secondary = Color(0xFF7DD3FC),
            onSecondary = Color(0xFF0B132B),
            secondaryContainer = Color(0xFF1C2541),
            onSecondaryContainer = Color(0xFFBAE6FD),
            tertiary = Color(0xFFBAE6FD),
            onTertiary = Color(0xFF0B132B),
            background = Color(0xFF080D1A),
            onBackground = SoftWhite,
            surface = Color(0xFF131D33),
            onSurface = SoftWhite,
            surfaceVariant = Color(0xFF1C2B4D),
            onSurfaceVariant = MutedGray,
            outline = Color(0x337DD3FC)
        )

        "aurora_purple" -> darkColorScheme(
            primary = Color(0xFF10B981),
            onPrimary = Color(0xFF0A1924),
            primaryContainer = Color(0xFF064E3B),
            onPrimaryContainer = Color(0xFFA7F3D0),
            secondary = Color(0xFF34D399),
            onSecondary = Color(0xFF0A1924),
            secondaryContainer = Color(0xFF112E3B),
            onSecondaryContainer = Color(0xFFA7F3D0),
            tertiary = Color(0xFF38BDF8),
            onTertiary = Color(0xFF0A1924),
            background = Color(0xFF08141D),
            onBackground = SoftWhite,
            surface = Color(0xFF0F2432),
            onSurface = SoftWhite,
            surfaceVariant = Color(0xFF19374B),
            onSurfaceVariant = MutedGray,
            outline = Color(0x3334D399)
        )

        "dynamic_album" -> {
            when (songMood?.uppercase()) {
                "HAPPY" -> darkColorScheme(
                    primary = Color(0xFFF59E0B),
                    onPrimary = Color(0xFF17132F),
                    secondary = Color(0xFFFCD34D),
                    onSecondary = Color(0xFF17132F),
                    tertiary = PastelPink,
                    background = Color(0xFF1A1426),
                    surface = Color(0xFF261D38),
                    outline = Color(0x40F59E0B)
                )
                "SAD" -> darkColorScheme(
                    primary = Color(0xFF60A5FA),
                    onPrimary = Color(0xFF17132F),
                    secondary = Color(0xFF93C5FD),
                    onSecondary = Color(0xFF17132F),
                    tertiary = Lavender,
                    background = Color(0xFF0E1428),
                    surface = Color(0xFF16203D),
                    outline = Color(0x4060A5FA)
                )
                "ROCK", "HYPE" -> darkColorScheme(
                    primary = Color(0xFFEC4899),
                    onPrimary = SoftWhite,
                    secondary = Color(0xFFF472B6),
                    onSecondary = Color(0xFF17132F),
                    tertiary = Color(0xFFA855F7),
                    background = Color(0xFF1F0D24),
                    surface = Color(0xFF2E1536),
                    outline = Color(0x40EC4899)
                )
                else -> darkColorScheme(
                    primary = PrimaryPurple,
                    onPrimary = SoftWhite,
                    secondary = Lavender,
                    onSecondary = DeepNightPurple,
                    tertiary = PastelPink,
                    background = DeepNightPurple,
                    surface = DarkSurface,
                    outline = GlassBorder
                )
            }
        }

        else -> darkColorScheme(
            primary = PrimaryPurple,
            onPrimary = SoftWhite,
            primaryContainer = MidnightViolet,
            onPrimaryContainer = SoftLavender,
            secondary = Lavender,
            onSecondary = DeepNightPurple,
            secondaryContainer = DarkSurfaceElevated,
            onSecondaryContainer = SoftLavender,
            tertiary = PastelPink,
            onTertiary = DeepNightPurple,
            background = DeepNightPurple,
            onBackground = SoftWhite,
            surface = DarkSurface,
            onSurface = SoftWhite,
            surfaceVariant = DarkSurfaceElevated,
            onSurfaceVariant = MutedGray,
            outline = GlassBorder
        )
    }
}

@Composable
fun LunaTuneTheme(
    wallpaperKey: String = "dreamy_purple",
    currentSongMood: String? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = getLunaColorScheme(wallpaperKey, currentSongMood)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LunaTuneTheme(content = content)
}

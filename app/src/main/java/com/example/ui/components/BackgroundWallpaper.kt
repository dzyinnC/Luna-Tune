package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

@Composable
fun BackgroundWallpaper(
    wallpaperKey: String,
    modifier: Modifier = Modifier
) {
    val bg = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface

    Box(modifier = modifier.fillMaxSize()) {
        when (wallpaperKey.lowercase()) {
            "soft_pastel", "sunset" -> {
                Image(
                    painter = painterResource(id = R.drawable.wallpaper_sunset),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(surface.copy(alpha = 0.85f), bg.copy(alpha = 0.94f))
                            )
                        )
                )
            }
            "meadow" -> {
                Image(
                    painter = painterResource(id = R.drawable.wallpaper_meadow),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(surface.copy(alpha = 0.85f), bg.copy(alpha = 0.94f))
                            )
                        )
                )
            }
            "aurora_purple" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF042F2E), Color(0xFF0F172A), Color(0xFF1E1B4B))
                            )
                        )
                )
            }
            "violet_galaxy" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF312E81), Color(0xFF0F172A), Color(0xFF020617))
                            )
                        )
                )
            }
            "moonlight" -> {
                Image(
                    painter = painterResource(id = R.drawable.wallpaper_night),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xE6081426), Color(0xF20B132B))
                            )
                        )
                )
            }
            else -> {
                // Default: Dreamy Purple / Lavender Night
                Image(
                    painter = painterResource(id = R.drawable.wallpaper_night),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(surface.copy(alpha = 0.82f), bg.copy(alpha = 0.94f))
                            )
                        )
                )
            }
        }
    }
}

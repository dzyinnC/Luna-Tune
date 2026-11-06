package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.character.EmotionState
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedGray
import com.example.ui.theme.PastelPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftWhite

@Composable
fun SettingsScreen(
    currentWallpaper: String,
    currentPerformance: String,
    isDynamicAlbumEnabled: Boolean,
    emotionState: EmotionState,
    onSelectWallpaper: (String) -> Unit,
    onToggleDynamicAlbum: (Boolean) -> Unit,
    onSelectPerformance: (String) -> Unit,
    onOpenExpressionSelector: () -> Unit,
    onScanAudio: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWallpaperDialog by remember { mutableStateOf(false) }
    var showPerformanceDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "Pengaturan",
            color = SoftWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Profile Banner: Dizzyn with Luna Avatar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF17132F))
                    .border(2.dp, Lavender, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = emotionState.currentExpression.iconRes),
                    contentDescription = "Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Dizzyn",
                    color = SoftWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33F4C9E8))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "👑 Premium Member",
                        color = PastelPink,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Unified Appearance & Settings Group
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x80211A46))
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
        ) {
            // UNIFIED: Wallpaper & Appearance (replaces separate Tema Aplikasi)
            SettingsRow(
                icon = Icons.Default.Palette,
                title = "Wallpaper & Appearance",
                subtitle = "Pilihan tema warna & background ($currentWallpaper)",
                onClick = { showWallpaperDialog = true }
            )

            // Dynamic Album Wallpaper Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF282054)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = PastelPink, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Dynamic Album Wallpaper", color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Warna UI adaptif sesuai musik yang diputar", color = MutedGray, fontSize = 11.5.sp)
                }

                Switch(
                    checked = isDynamicAlbumEnabled,
                    onCheckedChange = onToggleDynamicAlbum,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SoftWhite,
                        checkedTrackColor = PrimaryPurple,
                        uncheckedThumbColor = MutedGray,
                        uncheckedTrackColor = Color(0x33282054)
                    )
                )
            }

            SettingsRow(
                icon = Icons.Default.Face,
                title = "Karakter Luna",
                subtitle = "Koleksi 19 ekspresi & kunci manual",
                onClick = onOpenExpressionSelector
            )

            SettingsRow(
                icon = Icons.Default.Notifications,
                title = "Sleep Timer",
                subtitle = "Pengatur waktu pemutar otomatis",
                onClick = onOpenSleepTimer
            )

            SettingsRow(
                icon = Icons.Default.GraphicEq,
                title = "Pindai Media Lokal",
                subtitle = "Pindai musik dari penyimpanan perangkat",
                onClick = onScanAudio
            )

            SettingsRow(
                icon = Icons.Default.BatterySaver,
                title = "Mode Performa",
                subtitle = when (currentPerformance) {
                    "LOW" -> "Hemat Baterai (Low)"
                    "BALANCED" -> "Seimbang (Balanced)"
                    else -> "Premium Aesthetic (Full)"
                },
                onClick = { showPerformanceDialog = true }
            )

            SettingsRow(
                icon = Icons.Default.Info,
                title = "Tentang LunaTune",
                subtitle = "Versi 1.1.0 • Dream Edition",
                onClick = { showAboutDialog = true },
                isLast = true
            )
        }

        Spacer(modifier = Modifier.height(140.dp))
    }

    // Wallpaper & Appearance Modal Dialog
    if (showWallpaperDialog) {
        AlertDialog(
            onDismissRequest = { showWallpaperDialog = false },
            title = { Text("Wallpaper & Appearance", color = SoftWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf(
                        Pair("dreamy_purple", "Dreamy Purple (Default LunaTune)"),
                        Pair("lavender_night", "Lavender Night (Deep Violet)"),
                        Pair("violet_galaxy", "Violet Galaxy (Indigo Space)"),
                        Pair("soft_pastel", "Soft Pastel (Sakura Senja)"),
                        Pair("moonlight", "Moonlight (Blue Night)"),
                        Pair("aurora_purple", "Aurora Purple (Emerald Glow)")
                    ).forEach { (key, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectWallpaper(key)
                                    showWallpaperDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                color = if (currentWallpaper == key) PastelPink else SoftWhite,
                                fontWeight = if (currentWallpaper == key) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWallpaperDialog = false }) {
                    Text("Tutup", color = Lavender)
                }
            },
            containerColor = Color(0xFF282054)
        )
    }

    // Performance Dialog
    if (showPerformanceDialog) {
        AlertDialog(
            onDismissRequest = { showPerformanceDialog = false },
            title = { Text("Pilih Mode Performa", color = SoftWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("LOW", "BALANCED", "PREMIUM").forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectPerformance(mode)
                                    showPerformanceDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (mode) {
                                    "LOW" -> "Hemat Baterai (Animasi sederhana & background statis)"
                                    "BALANCED" -> "Seimbang (Performa & estetika stabil)"
                                    else -> "Premium Aesthetic (Animasi & efek glow penuh)"
                                },
                                color = if (currentPerformance == mode) PastelPink else SoftWhite,
                                fontWeight = if (currentPerformance == mode) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPerformanceDialog = false }) {
                    Text("Tutup", color = Lavender)
                }
            },
            containerColor = Color(0xFF282054)
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("LunaTune v1.1.0", color = SoftWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "LunaTune — Your Perfect Anime Music Companion.\n\n" +
                               "Fitur Utama:\n" +
                               "• Native Android Media3 Audio Engine (Offline & Legal Streaming)\n" +
                               "• 19 Ekspresi Interaktif Karakter Luna\n" +
                               "• Luna Focus Mode (Pomodoro Productivity)\n" +
                               "• Unified Wallpaper & Dynamic Color Adaptation\n" +
                               "• 5-Band Equalizer & Bass/Treble Knobs\n" +
                               "• Katalog Musik Legal Creative Commons & CC0",
                        color = SoftLavender,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Selesai", color = Lavender)
                }
            },
            containerColor = Color(0xFF282054)
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF282054)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Lavender, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = MutedGray, fontSize = 11.5.sp)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MutedGray.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
    }
}


package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.character.EmotionState
import com.example.pomodoro.PomodoroMode
import com.example.pomodoro.PomodoroState
import com.example.pomodoro.PomodoroStatus
import com.example.ui.components.LunaSpeechBubble
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedGray
import com.example.ui.theme.PastelPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftWhite

@Composable
fun PomodoroScreen(
    pomodoroState: PomodoroState,
    emotionState: EmotionState,
    onStartOrResume: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkipSession: () -> Unit,
    onSelectMode: (PomodoroMode) -> Unit,
    onSetCustomDurations: (Int, Int, Int) -> Unit,
    onPlayFocusMusic: () -> Unit,
    onTapLuna: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = pomodoroState.status == PomodoroStatus.RUNNING
    var showCustomDurationDialog by remember { mutableStateOf(false) }

    // Cloud gentle floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "pomo_cloud_motion")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_offset"
    )

    val minutes = pomodoroState.remainingSeconds / 60
    val seconds = pomodoroState.remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("pomodoro_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header Pomodoro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Luna Focus Mode",
                    color = SoftWhite,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dreamy Productivity Experience",
                    color = Lavender,
                    fontSize = 12.5.sp
                )
            }

            IconButton(
                onClick = { showCustomDurationDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x33282054))
                    .border(1.dp, GlassBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Custom Durations",
                    tint = SoftLavender,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Ilustrasi Luna tidur di atas awan / fokus
        Box(
            modifier = Modifier
                .size(130.dp)
                .offset(y = cloudOffset.dp)
                .clickable(onClick = onTapLuna)
                .testTag("pomodoro_luna_cloud"),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.luna_pomodoro_cloud),
                contentDescription = "Luna Sleeping on Cloud",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 3. Status Sesi & Progress Siklus
        Text(
            text = when (pomodoroState.mode) {
                PomodoroMode.FOCUS -> "Sesi Fokus • Siklus ke-${(pomodoroState.completedCycles % 4) + 1} dari 4"
                PomodoroMode.SHORT_BREAK -> "Waktunya Istirahat Pendek (5 min)"
                PomodoroMode.LONG_BREAK -> "Waktunya Istirahat Panjang (15 min)"
            },
            color = if (pomodoroState.mode == PomodoroMode.FOCUS) PastelPink else Color(0xFF67E8F9),
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4 & 5. Circular Countdown Timer with Glow & Start/Pause Control
        Box(
            modifier = Modifier
                .size(235.dp)
                .clip(CircleShape)
                .background(Color(0xFF211A46))
                .border(2.dp, GlassBorder, CircleShape)
                .testTag("pomodoro_timer_circle"),
            contentAlignment = Alignment.Center
        ) {
            // Track
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(215.dp),
                color = Color(0x2B7655D9),
                strokeWidth = 10.dp
            )

            // Neon glowing progress ring
            CircularProgressIndicator(
                progress = { pomodoroState.progress },
                modifier = Modifier.size(215.dp),
                color = if (pomodoroState.mode == PomodoroMode.FOCUS) Color(0xFF67E8F9) else PastelPink,
                strokeWidth = 10.dp
            )

            // Center Content: Time, Mode & Big Start/Pause Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timeFormatted,
                    color = SoftWhite,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = pomodoroState.mode.label,
                    color = Lavender,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Play / Pause Circle Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple)
                        .border(2.dp, SoftLavender, CircleShape)
                        .clickable(onClick = if (isRunning) onPause else onStartOrResume)
                        .testTag("pomodoro_play_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = SoftWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 6, 7, 8. Mode Selection Chips (Fokus, Istirahat, Istirahat Panjang)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PomodoroModeChip(
                title = "Fokus",
                duration = "${PomodoroMode.FOCUS.defaultMinutes} min",
                selected = pomodoroState.mode == PomodoroMode.FOCUS,
                onClick = { onSelectMode(PomodoroMode.FOCUS) }
            )
            PomodoroModeChip(
                title = "Istirahat",
                duration = "${PomodoroMode.SHORT_BREAK.defaultMinutes} min",
                selected = pomodoroState.mode == PomodoroMode.SHORT_BREAK,
                onClick = { onSelectMode(PomodoroMode.SHORT_BREAK) }
            )
            PomodoroModeChip(
                title = "Istirahat Panjang",
                duration = "${PomodoroMode.LONG_BREAK.defaultMinutes} min",
                selected = pomodoroState.mode == PomodoroMode.LONG_BREAK,
                onClick = { onSelectMode(PomodoroMode.LONG_BREAK) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Actions Row: Reset, Skip Session, Focus Music
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onReset)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = SoftLavender, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset", color = SoftLavender, fontSize = 12.5.sp)
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onSkipSession)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = "Skip", tint = PastelPink, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Lewati", color = PastelPink, fontSize = 12.5.sp)
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x4D7655D9))
                    .border(1.dp, Lavender.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable(onClick = onPlayFocusMusic)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = "Music", tint = SoftWhite, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Musik Fokus", color = SoftWhite, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Luna Speech Bubble
        LunaSpeechBubble(
            emotionState = emotionState,
            onTap = onTapLuna
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ringkasan Statistik
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x80211A46))
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${pomodoroState.todayFocusMinutes}m", color = PastelPink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Fokus Hari Ini", color = MutedGray, fontSize = 11.5.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${pomodoroState.completedCycles}", color = Lavender, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Siklus Selesai", color = MutedGray, fontSize = 11.5.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${pomodoroState.streakDays} Hari", color = Color(0xFF67E8F9), fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Streak Produktif", color = MutedGray, fontSize = 11.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }

    // Custom Duration Dialog
    if (showCustomDurationDialog) {
        var focusMin by remember { mutableFloatStateOf(25f) }
        var breakMin by remember { mutableFloatStateOf(5f) }
        var longBreakMin by remember { mutableFloatStateOf(15f) }

        AlertDialog(
            onDismissRequest = { showCustomDurationDialog = false },
            title = { Text("Atur Durasi Pomodoro", color = SoftWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Durasi Fokus: ${focusMin.toInt()} menit", color = SoftLavender, fontSize = 13.sp)
                    Slider(
                        value = focusMin,
                        onValueChange = { focusMin = it },
                        valueRange = 5f..60f,
                        steps = 10,
                        colors = SliderDefaults.colors(activeTrackColor = PrimaryPurple, thumbColor = SoftWhite)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Istirahat Pendek: ${breakMin.toInt()} menit", color = SoftLavender, fontSize = 13.sp)
                    Slider(
                        value = breakMin,
                        onValueChange = { breakMin = it },
                        valueRange = 1f..15f,
                        steps = 13,
                        colors = SliderDefaults.colors(activeTrackColor = PastelPink, thumbColor = SoftWhite)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Istirahat Panjang: ${longBreakMin.toInt()} menit", color = SoftLavender, fontSize = 13.sp)
                    Slider(
                        value = longBreakMin,
                        onValueChange = { longBreakMin = it },
                        valueRange = 5f..30f,
                        steps = 4,
                        colors = SliderDefaults.colors(activeTrackColor = Color(0xFF67E8F9), thumbColor = SoftWhite)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSetCustomDurations(focusMin.toInt(), breakMin.toInt(), longBreakMin.toInt())
                        showCustomDurationDialog = false
                    }
                ) {
                    Text("Simpan", color = PastelPink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDurationDialog = false }) {
                    Text("Batal", color = SoftLavender)
                }
            },
            containerColor = Color(0xFF282054)
        )
    }
}

@Composable
private fun PomodoroModeChip(
    title: String,
    duration: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) PrimaryPurple else Color(0x33282054))
            .border(
                1.dp,
                if (selected) Lavender else GlassBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = if (selected) SoftWhite else MutedGray,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = duration,
                color = if (selected) SoftLavender else MutedGray.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}


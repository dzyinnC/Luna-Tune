package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EqualizerPreset
import com.example.ui.components.AudioVisualizer
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.Lavender
import com.example.ui.theme.MidnightViolet
import com.example.ui.theme.MutedGray
import com.example.ui.theme.PastelPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    currentPreset: EqualizerPreset,
    bassLevel: Float,
    trebleLevel: Float,
    onPresetChange: (EqualizerPreset) -> Unit,
    onBassTrebleChange: (Float, Float) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var isEnabled by remember { mutableStateOf(true) }
    var bass by remember(bassLevel) { mutableFloatStateOf(bassLevel) }
    var treble by remember(trebleLevel) { mutableFloatStateOf(trebleLevel) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightViolet)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .testTag("equalizer_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SoftWhite
                )
            }

            Text(
                text = "Equalizer",
                color = SoftWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SoftWhite,
                    checkedTrackColor = PrimaryPurple,
                    uncheckedThumbColor = MutedGray,
                    uncheckedTrackColor = Color(0x33211A46)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real-time Visualizer bars
        AudioVisualizer(
            isPlaying = isEnabled,
            maxHeight = 64.dp,
            barCount = 32
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Presets Chips Grid
        Text(
            text = "Preset Efek Suara",
            color = SoftWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        val presets = EqualizerPreset.entries
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                presets.take(4).forEach { preset ->
                    EqualizerPresetChip(
                        preset = preset,
                        isSelected = currentPreset == preset,
                        onClick = { onPresetChange(preset) }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                presets.drop(4).take(4).forEach { preset ->
                    EqualizerPresetChip(
                        preset = preset,
                        isSelected = currentPreset == preset,
                        onClick = { onPresetChange(preset) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Rotary Style Knobs for Bass & Treble
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Bass Knob
            KnobControl(
                label = "Bass",
                value = bass,
                onValueChange = {
                    bass = it
                    onBassTrebleChange(bass, treble)
                }
            )

            // Treble Knob
            KnobControl(
                label = "Treble",
                value = treble,
                onValueChange = {
                    treble = it
                    onBassTrebleChange(bass, treble)
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Reset Button
        Button(
            onClick = {
                bass = 0f
                treble = 0f
                onPresetChange(EqualizerPreset.DEFAULT)
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(160.dp)
                .height(44.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = SoftWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset", color = SoftWhite, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun EqualizerPresetChip(
    preset: EqualizerPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) PrimaryPurple else Color(0x66211A46))
            .border(1.dp, if (isSelected) Lavender else GlassBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = preset.displayName,
            color = if (isSelected) SoftWhite else SoftLavender,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun KnobControl(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xFF282054))
                .border(2.dp, Lavender, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${value.toInt()} dB",
                    color = PastelPink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = SoftWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -10f..10f,
            colors = SliderDefaults.colors(
                thumbColor = SoftWhite,
                activeTrackColor = PrimaryPurple,
                inactiveTrackColor = Color(0x33B9A3F4)
            ),
            modifier = Modifier.width(130.dp)
        )
    }
}

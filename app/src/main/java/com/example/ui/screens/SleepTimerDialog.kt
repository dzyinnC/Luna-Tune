package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Lavender
import com.example.ui.theme.PastelPink
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftWhite

@Composable
fun SleepTimerDialog(
    currentMinutesRemaining: Int?,
    onSetTimer: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        Pair(15, "15 Menit"),
        Pair(30, "30 Menit"),
        Pair(45, "45 Menit"),
        Pair(60, "60 Menit")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Sleep Timer",
                color = SoftWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                if (currentMinutesRemaining != null && currentMinutesRemaining > 0) {
                    Text(
                        text = "Timer aktif: sisa $currentMinutesRemaining menit",
                        color = PastelPink,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                options.forEach { (minutes, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSetTimer(minutes)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentMinutesRemaining == minutes,
                            onClick = {
                                onSetTimer(minutes)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryPurple,
                                unselectedColor = Lavender
                            )
                        )
                        Text(
                            text = label,
                            color = SoftWhite,
                            fontSize = 14.5.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                if (currentMinutesRemaining != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSetTimer(null)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = {
                                onSetTimer(null)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryPurple,
                                unselectedColor = Lavender
                            )
                        )
                        Text(
                            text = "Matikan Sleep Timer",
                            color = PastelPink,
                            fontSize = 14.5.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = SoftLavender)
            }
        },
        containerColor = Color(0xFF282054)
    )
}

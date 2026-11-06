package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String = "LunaTune",
    val album: String = "Luna Melodies",
    val durationMs: Long = 180000L,
    val mediaUri: String,
    val albumArtRes: Int? = null,
    val genre: String = "Lo-Fi / Anime",
    val mood: String = "CHILL",
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L,
    val lyrics: String = "",
    val isOnline: Boolean = false,
    val streamUrl: String = "",
    val license: String = "CC0 Public Domain",
    val attribution: String = "LunaTune Studio",
    val bpm: Int? = null
)

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val coverRes: Int,
    val songCount: Int = 0,
    val isOnline: Boolean = false
)

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 25,
    val mode: String = "FOCUS", // FOCUS, SHORT_BREAK, LONG_BREAK
    val completed: Boolean = true
)

data class PomodoroStats(
    val todayFocusMinutes: Int = 50,
    val completedCycles: Int = 2,
    val streakDays: Int = 4,
    val totalFocusHours: Float = 14.5f
)

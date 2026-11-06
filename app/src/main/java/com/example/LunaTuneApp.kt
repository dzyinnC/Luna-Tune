package com.example

import android.app.Application
import com.example.character.LunaEmotionEngine
import com.example.data.db.LunaTuneDatabase
import com.example.data.pref.LunaTunePreferences
import com.example.data.repo.MusicRepository
import com.example.playback.LunaTunePlayerManager
import com.example.pomodoro.PomodoroEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LunaTuneApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var database: LunaTuneDatabase
        private set
    lateinit var musicRepository: MusicRepository
        private set
    lateinit var preferences: LunaTunePreferences
        private set
    lateinit var playerManager: LunaTunePlayerManager
        private set
    lateinit var emotionEngine: LunaEmotionEngine
        private set
    lateinit var pomodoroEngine: PomodoroEngine
        private set

    override fun onCreate() {
        super.onCreate()

        database = LunaTuneDatabase.getDatabase(this)
        musicRepository = MusicRepository(this)
        preferences = LunaTunePreferences(this)
        playerManager = LunaTunePlayerManager(this, applicationScope, musicRepository)
        emotionEngine = LunaEmotionEngine(applicationScope)
        pomodoroEngine = PomodoroEngine(database, applicationScope)

        // Preload default tracks & playlists
        applicationScope.launch {
            musicRepository.initializeDefaultLibraryIfNeeded()
        }

        // Bridge player state to EmotionEngine
        applicationScope.launch {
            playerManager.playbackState.collect { playback ->
                emotionEngine.updatePlaybackState(
                    playerState = playback.playerState,
                    mood = playback.currentSong?.mood ?: "CHILL"
                )
            }
        }

        // Bridge pomodoro state to EmotionEngine
        applicationScope.launch {
            pomodoroEngine.pomodoroState.collect { pomo ->
                emotionEngine.updatePomodoroState(
                    isActive = pomo.status == com.example.pomodoro.PomodoroStatus.RUNNING,
                    mode = pomo.mode.name
                )
            }
        }
    }

    override fun onTerminate() {
        playerManager.release()
        super.onTerminate()
    }
}

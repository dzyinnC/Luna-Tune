package com.example.playback

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.net.Uri
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.MainActivity
import com.example.data.model.EqualizerPreset
import com.example.data.model.PlayerState
import com.example.data.model.RepeatMode
import com.example.data.model.Song
import com.example.data.repo.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PlaybackState(
    val currentSong: Song? = null,
    val playerState: PlayerState = PlayerState.IDLE,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val playbackSpeed: Float = 1.0f,
    val equalizerPreset: EqualizerPreset = EqualizerPreset.DEFAULT,
    val bassLevel: Float = 0f,
    val trebleLevel: Float = 0f,
    val queue: List<Song> = emptyList(),
    val sleepTimerMinutesRemaining: Int? = null,
    val errorMessage: String? = null
)

class LunaTunePlayerManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val musicRepository: MusicRepository
) {
    var exoPlayer: ExoPlayer? = null
        private set

    var mediaSession: MediaSession? = null
        private set

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var progressTrackerJob: Job? = null
    private var sleepTimerJob: Job? = null

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val player = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        player.volume = 1.0f
        exoPlayer = player

        // Shared MediaSession for system notification & background audio
        val sessionActivityIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        mediaSession = MediaSession.Builder(context, player)
            .setSessionActivity(sessionActivityIntent)
            .build()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        _playbackState.value = _playbackState.value.copy(
                            playerState = PlayerState.LOADING,
                            errorMessage = null
                        )
                    }
                    Player.STATE_READY -> {
                        val isPlaying = player.isPlaying
                        val duration = player.duration.coerceAtLeast(0L)
                        _playbackState.value = _playbackState.value.copy(
                            playerState = if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED,
                            durationMs = if (duration > 0) duration else (_playbackState.value.currentSong?.durationMs ?: 0L),
                            errorMessage = null
                        )
                        initAudioEffectsSafely()
                    }
                    Player.STATE_ENDED -> {
                        onTrackEnded()
                    }
                    Player.STATE_IDLE -> {
                        _playbackState.value = _playbackState.value.copy(playerState = PlayerState.IDLE)
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.value = _playbackState.value.copy(
                    playerState = if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
                )
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("LunaTune", "ExoPlayer error occurred: ${error.errorCodeName}", error)
                _playbackState.value = _playbackState.value.copy(
                    playerState = PlayerState.ERROR,
                    errorMessage = "Gagal memutar audio: ${error.message ?: error.errorCodeName}"
                )
            }
        })
    }

    private fun initAudioEffectsSafely() {
        try {
            val sessionId = exoPlayer?.audioSessionId ?: C.AUDIO_SESSION_ID_UNSET
            if (sessionId != C.AUDIO_SESSION_ID_UNSET && equalizer == null) {
                equalizer = Equalizer(0, sessionId).apply {
                    enabled = true
                }
                bassBoost = BassBoost(0, sessionId).apply {
                    enabled = true
                }
            }
        } catch (e: Exception) {
            Log.w("LunaTune", "Audio effects not supported on this device/emulator: ${e.message}")
        }
    }

    fun playSong(song: Song, newQueue: List<Song>? = null) {
        val player = exoPlayer ?: return

        newQueue?.let {
            _playbackState.value = _playbackState.value.copy(queue = it)
        } ?: run {
            if (!_playbackState.value.queue.any { it.id == song.id }) {
                _playbackState.value = _playbackState.value.copy(
                    queue = _playbackState.value.queue + song
                )
            }
        }

        _playbackState.value = _playbackState.value.copy(
            currentSong = song,
            playerState = PlayerState.LOADING,
            currentPositionMs = 0L,
            durationMs = song.durationMs,
            errorMessage = null
        )

        scope.launch {
            try {
                // Resolves asset:/// to file:/// for 100% native stability, or keeps https:// stream URL
                val resolvedUri = musicRepository.resolveMediaUri(song.mediaUri)
                withContext(Dispatchers.Main) {
                    val mediaItem = MediaItem.fromUri(Uri.parse(resolvedUri))
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.volume = 1.0f
                    player.play()
                }
            } catch (e: Exception) {
                Log.e("LunaTune", "Failed to prepare song: ${song.title}", e)
                _playbackState.value = _playbackState.value.copy(
                    playerState = PlayerState.ERROR,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            val current = _playbackState.value.currentSong
            if (player.playbackState == Player.STATE_IDLE && current != null) {
                playSong(current)
            } else {
                player.play()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    fun next() {
        val queue = _playbackState.value.queue
        if (queue.isEmpty()) return

        val currentIndex = queue.indexOfFirst { it.id == _playbackState.value.currentSong?.id }
        val nextIndex = if (_playbackState.value.isShuffleEnabled) {
            queue.indices.random()
        } else {
            if (currentIndex in 0 until queue.lastIndex) currentIndex + 1 else 0
        }
        playSong(queue[nextIndex])
    }

    fun previous() {
        val queue = _playbackState.value.queue
        if (queue.isEmpty()) return

        if ((exoPlayer?.currentPosition ?: 0L) > 3000L) {
            seekTo(0L)
            return
        }

        val currentIndex = queue.indexOfFirst { it.id == _playbackState.value.currentSong?.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else queue.lastIndex
        playSong(queue[prevIndex])
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(
            isShuffleEnabled = !_playbackState.value.isShuffleEnabled
        )
    }

    fun toggleRepeat() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = nextMode)
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.playbackParameters = PlaybackParameters(speed)
        _playbackState.value = _playbackState.value.copy(playbackSpeed = speed)
    }

    fun setEqualizerPreset(preset: EqualizerPreset) {
        _playbackState.value = _playbackState.value.copy(
            equalizerPreset = preset,
            bassLevel = preset.bass,
            trebleLevel = preset.treble
        )
        applyEqualizerPreset(preset)
    }

    fun setBassTreble(bass: Float, treble: Float) {
        _playbackState.value = _playbackState.value.copy(
            bassLevel = bass,
            trebleLevel = treble,
            equalizerPreset = EqualizerPreset.CUSTOM
        )
        try {
            val boostVal = ((bass.coerceIn(0f, 10f) / 10f) * 1000).toInt().toShort()
            bassBoost?.setStrength(boostVal)
        } catch (e: Exception) {
            Log.w("LunaTune", "Could not set bass boost: ${e.message}")
        }
    }

    private fun applyEqualizerPreset(preset: EqualizerPreset) {
        try {
            equalizer?.let { eq ->
                val numBands = eq.numberOfBands.toInt().coerceAtMost(preset.bands.size)
                val minLevel = eq.bandLevelRange[0]
                val maxLevel = eq.bandLevelRange[1]
                val span = maxLevel - minLevel

                for (i in 0 until numBands) {
                    val db = preset.bands[i]
                    val normalized = (db + 10f) / 20f
                    val level = (minLevel + (normalized * span)).toInt().toShort()
                    eq.setBandLevel(i.toShort(), level)
                }
            }
            bassBoost?.let { bb ->
                val boostVal = ((preset.bass.coerceIn(0f, 10f) / 10f) * 1000).toInt().toShort()
                bb.setStrength(boostVal)
            }
        } catch (e: Exception) {
            Log.w("LunaTune", "Could not apply equalizer preset: ${e.message}")
        }
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            _playbackState.value = _playbackState.value.copy(sleepTimerMinutesRemaining = null)
            return
        }

        _playbackState.value = _playbackState.value.copy(sleepTimerMinutesRemaining = minutes)
        sleepTimerJob = scope.launch(Dispatchers.Default) {
            var remaining = minutes
            while (remaining > 0 && isActive) {
                delay(60000L)
                remaining--
                _playbackState.value = _playbackState.value.copy(sleepTimerMinutesRemaining = remaining)
            }
            withContext(Dispatchers.Main) {
                exoPlayer?.pause()
            }
            _playbackState.value = _playbackState.value.copy(sleepTimerMinutesRemaining = null)
        }
    }

    private fun onTrackEnded() {
        when (_playbackState.value.repeatMode) {
            RepeatMode.ONE -> {
                seekTo(0L)
                exoPlayer?.play()
            }
            RepeatMode.ALL -> next()
            RepeatMode.OFF -> {
                val queue = _playbackState.value.queue
                val currentIndex = queue.indexOfFirst { it.id == _playbackState.value.currentSong?.id }
                if (currentIndex in 0 until queue.lastIndex) {
                    next()
                } else {
                    _playbackState.value = _playbackState.value.copy(playerState = PlayerState.PAUSED)
                }
            }
        }
    }

    private fun startProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                exoPlayer?.let { player ->
                    val pos = player.currentPosition.coerceAtLeast(0L)
                    val dur = player.duration.coerceAtLeast(0L)
                    _playbackState.value = _playbackState.value.copy(
                        currentPositionMs = pos,
                        durationMs = if (dur > 0) dur else (_playbackState.value.currentSong?.durationMs ?: 0L)
                    )
                }
                delay(300L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }

    fun release() {
        stopProgressTracker()
        sleepTimerJob?.cancel()
        equalizer?.release()
        bassBoost?.release()
        mediaSession?.release()
        exoPlayer?.release()
        mediaSession = null
        exoPlayer = null
    }
}

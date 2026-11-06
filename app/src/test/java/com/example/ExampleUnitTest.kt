package com.example

import com.example.character.LunaEmotionEngine
import com.example.data.model.EqualizerPreset
import com.example.data.model.LunaExpression
import com.example.data.model.PlayerState
import com.example.data.model.RepeatMode
import com.example.data.model.Song
import com.example.playback.PlaybackState
import com.example.pomodoro.PomodoroMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Test
    fun emotionEngine_manualLockPriority() = testScope.runTest {
        val engine = LunaEmotionEngine(this, enableBlinking = false)

        // Lock to SAD
        engine.setLockedExpression(LunaExpression.SAD)
        assertEquals(LunaExpression.SAD, engine.emotionState.value.currentExpression)

        // Even if playback is playing upbeat pop
        engine.updatePlaybackState(PlayerState.PLAYING, "HAPPY")
        assertEquals(LunaExpression.SAD, engine.emotionState.value.currentExpression)

        // Unlock
        engine.setLockedExpression(null)
        engine.updatePlaybackState(PlayerState.PLAYING, "HAPPY")
        assertEquals(LunaExpression.HAPPY, engine.emotionState.value.currentExpression)
    }

    @Test
    fun emotionEngine_moodMapping() = testScope.runTest {
        val engine = LunaEmotionEngine(this, enableBlinking = false)

        engine.updatePlaybackState(PlayerState.PLAYING, "CHILL")
        assertEquals(LunaExpression.ENJOYING_MUSIC, engine.emotionState.value.currentExpression)

        engine.updatePlaybackState(PlayerState.PLAYING, "ROCK")
        assertEquals(LunaExpression.HYPE, engine.emotionState.value.currentExpression)

        engine.updatePlaybackState(PlayerState.PLAYING, "ROMANTIC")
        assertEquals(LunaExpression.ROMANTIC, engine.emotionState.value.currentExpression)

        engine.updatePlaybackState(PlayerState.PLAYING, "GALAU")
        assertEquals(LunaExpression.CRYING, engine.emotionState.value.currentExpression)
    }

    @Test
    fun playbackState_defaultsAndToggles() {
        val sampleSong = Song(
            id = "track_1",
            title = "Better Days",
            mediaUri = "asset:///music/better_days.wav"
        )
        val state = PlaybackState(
            currentSong = sampleSong,
            playerState = PlayerState.PLAYING,
            currentPositionMs = 30000L,
            durationMs = 180000L,
            isShuffleEnabled = true,
            repeatMode = RepeatMode.ALL,
            equalizerPreset = EqualizerPreset.BASS_BOOST
        )

        assertNotNull(state.currentSong)
        assertEquals("Better Days", state.currentSong?.title)
        assertTrue(state.isShuffleEnabled)
        assertEquals(RepeatMode.ALL, state.repeatMode)
        assertEquals(EqualizerPreset.BASS_BOOST, state.equalizerPreset)
        assertEquals(8f, state.equalizerPreset.bass, 0.01f)
    }

    @Test
    fun pomodoroMode_defaultMinutes() {
        assertEquals(25, PomodoroMode.FOCUS.defaultMinutes)
        assertEquals(5, PomodoroMode.SHORT_BREAK.defaultMinutes)
        assertEquals(15, PomodoroMode.LONG_BREAK.defaultMinutes)
    }

    @Test
    fun lunaExpression_allNineteenExist() {
        assertEquals(19, LunaExpression.entries.size)
        val calm = LunaExpression.fromId("calm")
        assertEquals(LunaExpression.CALM, calm)
        val unknownFallback = LunaExpression.fromId("non_existent")
        assertEquals(LunaExpression.ENJOYING_MUSIC, unknownFallback)
    }
}

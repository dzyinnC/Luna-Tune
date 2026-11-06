package com.example.character

import com.example.data.model.LunaExpression
import com.example.data.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmotionState(
    val currentExpression: LunaExpression = LunaExpression.ENJOYING_MUSIC,
    val dialogueText: String = "Tetap semangat ya! Kamu pasti bisa! 💖",
    val isBlinking: Boolean = false,
    val isHeadBobbing: Boolean = true,
    val auraIntensity: Float = 0.8f
)

class LunaEmotionEngine(
    private val scope: CoroutineScope,
    enableBlinking: Boolean = true
) {
    private val _emotionState = MutableStateFlow(EmotionState())
    val emotionState: StateFlow<EmotionState> = _emotionState.asStateFlow()

    private var lockedExpression: LunaExpression? = null
    private var interactionJob: Job? = null
    private var lastUpdateTime = 0L
    private val minCooldownMs = 3000L

    private var currentPlaybackState: PlayerState = PlayerState.IDLE
    private var currentMusicMood: String = "CHILL"
    private var isPomodoroActive: Boolean = false
    private var pomodoroMode: String = "FOCUS"

    init {
        if (enableBlinking) {
            startIdleBlinkingLoop()
        }
    }

    fun setLockedExpression(expression: LunaExpression?) {
        lockedExpression = expression
        evaluateEmotion()
    }

    fun updatePlaybackState(playerState: PlayerState, mood: String) {
        currentPlaybackState = playerState
        currentMusicMood = mood
        evaluateEmotion()
    }

    fun updatePomodoroState(isActive: Boolean, mode: String) {
        isPomodoroActive = isActive
        pomodoroMode = mode
        evaluateEmotion()
    }

    fun onUserTapLuna() {
        interactionJob?.cancel()
        val tapReactions = listOf(
            Pair(LunaExpression.SHY, "Ehehe... Kamu menggelitik Luna! ♡"),
            Pair(LunaExpression.HAPPY, "Hehe, Luna senang bisa menemanimu belajar!"),
            Pair(LunaExpression.SMUG, "Lagu yang kita dengarkan keren banget kan?"),
            Pair(LunaExpression.TOUCHED, "Terima kasih sudah selalu ada bersama Luna ♡")
        )
        val selected = tapReactions.random()
        _emotionState.value = _emotionState.value.copy(
            currentExpression = selected.first,
            dialogueText = selected.second,
            auraIntensity = 1.0f
        )

        interactionJob = scope.launch {
            delay(5000L)
            evaluateEmotion()
        }
    }

    fun evaluateEmotion() {
        val now = System.currentTimeMillis()
        if (now - lastUpdateTime < minCooldownMs && interactionJob?.isActive == true) {
            return
        }
        lastUpdateTime = now

        // Priority 1: Manual Expression Lock
        if (lockedExpression != null) {
            val expr = lockedExpression!!
            _emotionState.value = _emotionState.value.copy(
                currentExpression = expr,
                dialogueText = expr.defaultDialogue,
                auraIntensity = 0.7f
            )
            return
        }

        // Priority 2: Pomodoro State
        if (isPomodoroActive) {
            val expr = if (pomodoroMode == "FOCUS") {
                LunaExpression.FOCUSED
            } else {
                LunaExpression.SLEEPY
            }
            val text = if (pomodoroMode == "FOCUS") {
                "Ayo fokus! Kamu pasti bisa! 💪"
            } else {
                "Waktunya istirahat sejenak~ Tarik nafas ya."
            }
            _emotionState.value = _emotionState.value.copy(
                currentExpression = expr,
                dialogueText = text,
                auraIntensity = 0.9f
            )
            return
        }

        // Priority 3: Playback State
        when (currentPlaybackState) {
            PlayerState.LOADING -> {
                _emotionState.value = _emotionState.value.copy(
                    currentExpression = LunaExpression.SURPRISED,
                    dialogueText = "Sedang menyiapkan lagumu...",
                    auraIntensity = 0.5f
                )
                return
            }
            PlayerState.ERROR -> {
                _emotionState.value = _emotionState.value.copy(
                    currentExpression = LunaExpression.WORRIED,
                    dialogueText = "Waduh, audio bermasalah. Coba lagu lain yuk?",
                    auraIntensity = 0.4f
                )
                return
            }
            PlayerState.PAUSED, PlayerState.IDLE -> {
                _emotionState.value = _emotionState.value.copy(
                    currentExpression = LunaExpression.CALM,
                    dialogueText = "Musik dijeda. Mau lanjut mendengarkan kapan saja, Luna siap!",
                    auraIntensity = 0.4f
                )
                return
            }
            PlayerState.PLAYING -> {
                // Priority 4: Music Mood mapping
                val expr = when (currentMusicMood.uppercase()) {
                    "CHILL" -> LunaExpression.ENJOYING_MUSIC
                    "HAPPY" -> LunaExpression.HAPPY
                    "ROCK" -> LunaExpression.HYPE
                    "ROMANTIC" -> LunaExpression.ROMANTIC
                    "SAD" -> LunaExpression.SAD
                    "GALAU" -> LunaExpression.CRYING
                    "DANGDUT" -> LunaExpression.HYPE
                    else -> LunaExpression.ENJOYING_MUSIC
                }
                _emotionState.value = _emotionState.value.copy(
                    currentExpression = expr,
                    dialogueText = expr.defaultDialogue,
                    auraIntensity = 0.85f
                )
            }
        }
    }

    private fun startIdleBlinkingLoop() {
        scope.launch(Dispatchers.Default) {
            while (true) {
                delay((3000..7000).random().toLong())
                _emotionState.value = _emotionState.value.copy(isBlinking = true)
                delay(150L)
                _emotionState.value = _emotionState.value.copy(isBlinking = false)

                // Occasional double blink
                if ((0..3).random() == 0) {
                    delay(100L)
                    _emotionState.value = _emotionState.value.copy(isBlinking = true)
                    delay(150L)
                    _emotionState.value = _emotionState.value.copy(isBlinking = false)
                }
            }
        }
    }
}

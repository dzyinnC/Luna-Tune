package com.example.pomodoro

import android.os.SystemClock
import com.example.data.db.LunaTuneDatabase
import com.example.data.model.PomodoroSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class PomodoroMode(val label: String, val defaultMinutes: Int) {
    FOCUS("Fokus Belajar", 25),
    SHORT_BREAK("Istirahat Pendek", 5),
    LONG_BREAK("Istirahat Panjang", 15)
}

enum class PomodoroStatus {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

data class PomodoroState(
    val mode: PomodoroMode = PomodoroMode.FOCUS,
    val status: PomodoroStatus = PomodoroStatus.IDLE,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val progress: Float = 0f,
    val completedCycles: Int = 0,
    val todayFocusMinutes: Int = 50,
    val streakDays: Int = 4
)

class PomodoroEngine(
    private val database: LunaTuneDatabase,
    private val scope: CoroutineScope
) {
    private val _pomodoroState = MutableStateFlow(PomodoroState())
    val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()

    private var targetEndTimeMs = 0L
    private var pausedRemainingSeconds = 25 * 60
    private var timerJob: Job? = null

    var focusMinutes = 25
    var shortBreakMinutes = 5
    var longBreakMinutes = 15

    init {
        resetToMode(PomodoroMode.FOCUS)
    }

    fun setMode(mode: PomodoroMode) {
        timerJob?.cancel()
        val durationMin = when (mode) {
            PomodoroMode.FOCUS -> focusMinutes
            PomodoroMode.SHORT_BREAK -> shortBreakMinutes
            PomodoroMode.LONG_BREAK -> longBreakMinutes
        }
        val totalSec = durationMin * 60
        pausedRemainingSeconds = totalSec
        _pomodoroState.value = _pomodoroState.value.copy(
            mode = mode,
            status = PomodoroStatus.IDLE,
            remainingSeconds = totalSec,
            totalSeconds = totalSec,
            progress = 0f
        )
    }

    fun startOrResume() {
        if (_pomodoroState.value.status == PomodoroStatus.RUNNING) return

        targetEndTimeMs = SystemClock.elapsedRealtime() + (pausedRemainingSeconds * 1000L)
        _pomodoroState.value = _pomodoroState.value.copy(status = PomodoroStatus.RUNNING)

        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                val now = SystemClock.elapsedRealtime()
                val diffMs = targetEndTimeMs - now
                if (diffMs <= 0) {
                    onSessionFinished()
                    break
                }
                val secRemaining = (diffMs / 1000).toInt()
                val total = _pomodoroState.value.totalSeconds
                val progress = ((total - secRemaining).toFloat() / total.toFloat()).coerceIn(0f, 1f)

                _pomodoroState.value = _pomodoroState.value.copy(
                    remainingSeconds = secRemaining,
                    progress = progress
                )
                delay(500L)
            }
        }
    }

    fun pause() {
        if (_pomodoroState.value.status != PomodoroStatus.RUNNING) return
        timerJob?.cancel()
        val now = SystemClock.elapsedRealtime()
        pausedRemainingSeconds = ((targetEndTimeMs - now) / 1000).toInt().coerceAtLeast(0)
        _pomodoroState.value = _pomodoroState.value.copy(
            status = PomodoroStatus.PAUSED,
            remainingSeconds = pausedRemainingSeconds
        )
    }

    fun reset() {
        setMode(_pomodoroState.value.mode)
    }

    fun skipSession() {
        timerJob?.cancel()
        val currentMode = _pomodoroState.value.mode
        val wasFocus = currentMode == PomodoroMode.FOCUS
        val nextMode = if (wasFocus) PomodoroMode.SHORT_BREAK else PomodoroMode.FOCUS
        setMode(nextMode)
    }

    fun setCustomDurations(focus: Int, sBreak: Int, lBreak: Int) {
        focusMinutes = focus
        shortBreakMinutes = sBreak
        longBreakMinutes = lBreak
        setMode(_pomodoroState.value.mode)
    }

    private fun resetToMode(mode: PomodoroMode) {
        setMode(mode)
    }

    private fun onSessionFinished() {
        val currentMode = _pomodoroState.value.mode
        val wasFocus = currentMode == PomodoroMode.FOCUS
        val newCycles = if (wasFocus) _pomodoroState.value.completedCycles + 1 else _pomodoroState.value.completedCycles

        // Save session to database
        scope.launch(Dispatchers.IO) {
            database.pomodoroDao().insertSession(
                PomodoroSession(
                    durationMinutes = currentMode.defaultMinutes,
                    mode = currentMode.name,
                    completed = true
                )
            )
        }

        val nextMode = if (wasFocus) {
            if (newCycles % 4 == 0) PomodoroMode.LONG_BREAK else PomodoroMode.SHORT_BREAK
        } else {
            PomodoroMode.FOCUS
        }

        val addedMinutes = if (wasFocus) focusMinutes else 0

        _pomodoroState.value = _pomodoroState.value.copy(
            status = PomodoroStatus.COMPLETED,
            remainingSeconds = 0,
            progress = 1f,
            completedCycles = newCycles,
            todayFocusMinutes = _pomodoroState.value.todayFocusMinutes + addedMinutes
        )

        // Auto prepare next mode
        setMode(nextMode)
    }
}

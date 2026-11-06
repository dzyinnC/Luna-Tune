package com.example.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lunatune_settings")

class LunaTunePreferences(private val context: Context) {
    companion object {
        val KEY_WALLPAPER = stringPreferencesKey("pref_wallpaper")
        val KEY_PERFORMANCE = stringPreferencesKey("pref_performance")
        val KEY_DIALOG_ENABLED = booleanPreferencesKey("pref_dialog_enabled")
        val KEY_LOCKED_EXPRESSION = stringPreferencesKey("pref_locked_expression")
        val KEY_POMODORO_FOCUS_MIN = intPreferencesKey("pref_pomo_focus")
        val KEY_POMODORO_BREAK_MIN = intPreferencesKey("pref_pomo_break")
        val KEY_POMODORO_LONG_BREAK_MIN = intPreferencesKey("pref_pomo_long_break")
        val KEY_EQ_PRESET = stringPreferencesKey("pref_eq_preset")
        val KEY_EQ_BASS = floatPreferencesKey("pref_eq_bass")
        val KEY_EQ_TREBLE = floatPreferencesKey("pref_eq_treble")
        val KEY_DYNAMIC_ALBUM_ENABLED = booleanPreferencesKey("pref_dynamic_album_enabled")
    }

    val selectedWallpaper: Flow<String> = context.dataStore.data.map {
        it[KEY_WALLPAPER] ?: "dreamy_purple"
    }

    val performanceMode: Flow<String> = context.dataStore.data.map {
        it[KEY_PERFORMANCE] ?: "PREMIUM"
    }

    val isDialogEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_DIALOG_ENABLED] ?: true
    }

    val isDynamicAlbumEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[KEY_DYNAMIC_ALBUM_ENABLED] ?: false
    }

    val lockedExpression: Flow<String?> = context.dataStore.data.map {
        it[KEY_LOCKED_EXPRESSION]
    }

    val pomodoroFocusMinutes: Flow<Int> = context.dataStore.data.map {
        it[KEY_POMODORO_FOCUS_MIN] ?: 25
    }

    val pomodoroBreakMinutes: Flow<Int> = context.dataStore.data.map {
        it[KEY_POMODORO_BREAK_MIN] ?: 5
    }

    val pomodoroLongBreakMinutes: Flow<Int> = context.dataStore.data.map {
        it[KEY_POMODORO_LONG_BREAK_MIN] ?: 15
    }

    suspend fun setWallpaper(wallpaper: String) {
        context.dataStore.edit { it[KEY_WALLPAPER] = wallpaper }
    }

    suspend fun setPerformanceMode(mode: String) {
        context.dataStore.edit { it[KEY_PERFORMANCE] = mode }
    }

    suspend fun setDialogEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DIALOG_ENABLED] = enabled }
    }

    suspend fun setDynamicAlbumEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DYNAMIC_ALBUM_ENABLED] = enabled }
    }

    suspend fun setLockedExpression(expressionId: String?) {
        context.dataStore.edit {
            if (expressionId != null) {
                it[KEY_LOCKED_EXPRESSION] = expressionId
            } else {
                it.remove(KEY_LOCKED_EXPRESSION)
            }
        }
    }

    suspend fun setPomodoroDurations(focus: Int, sBreak: Int, lBreak: Int) {
        context.dataStore.edit {
            it[KEY_POMODORO_FOCUS_MIN] = focus
            it[KEY_POMODORO_BREAK_MIN] = sBreak
            it[KEY_POMODORO_LONG_BREAK_MIN] = lBreak
        }
    }
}

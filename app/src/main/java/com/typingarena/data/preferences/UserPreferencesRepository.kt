package com.typingarena.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.typingarena.data.model.Difficulty
import com.typingarena.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val DEFAULT_DIFFICULTY = stringPreferencesKey("default_difficulty")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            themeMode = preferences[Keys.THEME_MODE] ?: "SYSTEM",
            fontSizeSp = preferences[Keys.FONT_SIZE] ?: 18f,
            soundEnabled = preferences[Keys.SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[Keys.VIBRATION_ENABLED] ?: true,
            defaultDifficulty = try {
                Difficulty.valueOf(preferences[Keys.DEFAULT_DIFFICULTY] ?: "MEDIUM")
            } catch (e: Exception) {
                Difficulty.MEDIUM
            }
        )
    }

    suspend fun updateThemeMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[Keys.THEME_MODE] = mode }
    }

    suspend fun updateFontSize(sizeSp: Float) {
        context.dataStore.edit { prefs -> prefs[Keys.FONT_SIZE] = sizeSp }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun updateDefaultDifficulty(difficulty: Difficulty) {
        context.dataStore.edit { prefs -> prefs[Keys.DEFAULT_DIFFICULTY] = difficulty.name }
    }
}

package com.typingarena.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.typingarena.data.model.Difficulty
import com.typingarena.data.model.UserSettings
import com.typingarena.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserPreferencesRepository(application)

    val userSettings: StateFlow<UserSettings> = repo.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    fun setThemeMode(mode: String) {
        viewModelScope.launch { repo.updateThemeMode(mode) }
    }

    fun setFontSize(sizeSp: Float) {
        viewModelScope.launch { repo.updateFontSize(sizeSp) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { repo.updateSoundEnabled(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { repo.updateVibrationEnabled(enabled) }
    }

    fun setDefaultDifficulty(difficulty: Difficulty) {
        viewModelScope.launch { repo.updateDefaultDifficulty(difficulty) }
    }
}

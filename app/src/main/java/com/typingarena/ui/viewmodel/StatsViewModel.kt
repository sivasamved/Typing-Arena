package com.typingarena.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.typingarena.data.db.AppDatabase
import com.typingarena.data.model.TypingResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).typingResultDao()

    val allResults: StateFlow<List<TypingResult>> = dao.getAllResults()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highestWpm: StateFlow<Int?> = dao.getHighestWpm()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageWpm: StateFlow<Float?> = dao.getAverageWpm()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val averageAccuracy: StateFlow<Float?> = dao.getAverageAccuracy()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val totalTests: StateFlow<Int> = dao.getTotalTestsCompleted()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalTimeSeconds: StateFlow<Long?> = dao.getTotalTimePracticedSeconds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun clearStatistics() {
        viewModelScope.launch {
            dao.clearAllResults()
        }
    }
}

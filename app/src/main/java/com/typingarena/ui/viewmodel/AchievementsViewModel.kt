package com.typingarena.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.typingarena.data.db.AppDatabase
import com.typingarena.data.model.Achievement
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AchievementsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).typingResultDao()

    val achievements: StateFlow<List<Achievement>> = dao.getAllResults().map { results ->
        val totalTests = results.size
        val maxWpm = results.maxOfOrNull { it.wpm } ?: 0
        val maxAccuracy = results.maxOfOrNull { it.accuracy } ?: 0f

        listOf(
            Achievement(
                id = "first_test",
                title = "First Step",
                description = "Complete your very first typing test.",
                iconName = "EmojiEvents",
                isUnlocked = totalTests >= 1,
                progress = totalTests,
                target = 1
            ),
            Achievement(
                id = "wpm_50",
                title = "Speed Demon",
                description = "Reach a speed of 50 Words Per Minute.",
                iconName = "Speed",
                isUnlocked = maxWpm >= 50,
                progress = maxWpm,
                target = 50
            ),
            Achievement(
                id = "wpm_80",
                title = "Velocity Master",
                description = "Reach a speed of 80 Words Per Minute.",
                iconName = "Bolt",
                isUnlocked = maxWpm >= 80,
                progress = maxWpm,
                target = 80
            ),
            Achievement(
                id = "wpm_100",
                title = "Typing Legend",
                description = "Break the 100 Words Per Minute barrier!",
                iconName = "MilitaryTech",
                isUnlocked = maxWpm >= 100,
                progress = maxWpm,
                target = 100
            ),
            Achievement(
                id = "accuracy_95",
                title = "Sniper Precision",
                description = "Complete a test with at least 95% accuracy.",
                iconName = "TrackChanges",
                isUnlocked = maxAccuracy >= 95f,
                progress = maxAccuracy.toInt(),
                target = 95
            ),
            Achievement(
                id = "tests_100",
                title = "Century Club",
                description = "Complete 100 total typing tests.",
                iconName = "WorkspacePremium",
                isUnlocked = totalTests >= 100,
                progress = totalTests,
                target = 100
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

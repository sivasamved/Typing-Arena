package com.typingarena.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Difficulty {
    EASY, MEDIUM, HARD;

    fun displayName(): String = when(this) {
        EASY -> "Easy"
        MEDIUM -> "Medium"
        HARD -> "Hard"
    }
}

enum class PerformanceRating {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT;

    fun displayName(): String = when(this) {
        BEGINNER -> "Beginner"
        INTERMEDIATE -> "Intermediate"
        ADVANCED -> "Advanced"
        EXPERT -> "Expert"
    }

    companion object {
        fun fromWpm(wpm: Int): PerformanceRating {
            return when {
                wpm < 35 -> BEGINNER
                wpm < 65 -> INTERMEDIATE
                wpm < 95 -> ADVANCED
                else -> EXPERT
            }
        }
    }
}

@Entity(tableName = "typing_results")
data class TypingResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wpm: Int,
    val accuracy: Float,
    val totalWords: Int,
    val correctWords: Int,
    val incorrectWords: Int,
    val totalMistakes: Int,
    val totalCharacters: Int,
    val durationSeconds: Int,
    val difficulty: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val target: Int
)

data class UserSettings(
    val themeMode: String = "SYSTEM", // "LIGHT", "DARK", "SYSTEM"
    val fontSizeSp: Float = 18f,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val defaultDifficulty: Difficulty = Difficulty.MEDIUM
)

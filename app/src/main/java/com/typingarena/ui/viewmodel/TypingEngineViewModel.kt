package com.typingarena.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.typingarena.data.db.AppDatabase
import com.typingarena.data.model.Difficulty
import com.typingarena.data.model.TypingResult
import com.typingarena.data.repository.ParagraphRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

enum class CharacterState { UNTYPED, CORRECT, INCORRECT }

data class TypingTestUiState(
    val targetText: String = "",
    val userInput: String = "",
    val durationSeconds: Int = 30,
    val timeRemainingSeconds: Int = 30,
    val isPracticeMode: Boolean = false,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val currentWpm: Int = 0,
    val accuracy: Float = 100f,
    val totalMistakes: Int = 0,
    val correctWords: Int = 0,
    val incorrectWords: Int = 0,
    val totalWords: Int = 0,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val lastSavedResultId: Long? = null
)

class TypingEngineViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).typingResultDao()

    private val _uiState = MutableStateFlow(TypingTestUiState())
    val uiState: StateFlow<TypingTestUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTimeMillis: Long = 0

    fun startTest(durationSeconds: Int = 30, difficulty: Difficulty = Difficulty.MEDIUM, isPractice: Boolean = false) {
        val text = ParagraphRepository.getRandomParagraph(difficulty)
        _uiState.value = TypingTestUiState(
            targetText = text,
            userInput = "",
            durationSeconds = durationSeconds,
            timeRemainingSeconds = durationSeconds,
            isPracticeMode = isPractice,
            isRunning = false,
            isFinished = false,
            difficulty = difficulty
        )
    }

    fun onInputTyped(newInput: String) {
        val currentState = _uiState.value
        if (currentState.isFinished) return

        // Auto start timer on first character typed
        if (!currentState.isRunning && newInput.isNotEmpty()) {
            startTimer()
        }

        val targetText = currentState.targetText
        var mistakes = currentState.totalMistakes

        // Count mistakes if last typed character didn't match target
        if (newInput.length > currentState.userInput.length) {
            val typedIndex = newInput.length - 1
            if (typedIndex < targetText.length && newInput[typedIndex] != targetText[typedIndex]) {
                mistakes++
            }
        }

        // Calculate live WPM & accuracy
        val elapsedSeconds = max(1, (currentState.durationSeconds - currentState.timeRemainingSeconds))
        val minutes = elapsedSeconds / 60.0
        
        var correctChars = 0
        for (i in newInput.indices) {
            if (i < targetText.length && newInput[i] == targetText[i]) {
                correctChars++
            }
        }

        // Standard WPM formula: (Correct Chars / 5) / Minutes
        val wpm = if (minutes > 0) ((correctChars / 5.0) / minutes).toInt() else 0
        val accuracy = if (newInput.isNotEmpty()) (correctChars.toFloat() / newInput.length) * 100f else 100f

        _uiState.value = currentState.copy(
            userInput = newInput,
            currentWpm = max(0, wpm),
            accuracy = accuracy,
            totalMistakes = mistakes
        )

        // Check if finished entire paragraph
        if (newInput.length >= targetText.length) {
            finishTest()
        }
    }

    private fun startTimer() {
        startTimeMillis = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(isRunning = true)

        if (_uiState.value.isPracticeMode) return

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemainingSeconds > 0 && _uiState.value.isRunning && !_uiState.value.isFinished) {
                delay(1000)
                val remaining = _uiState.value.timeRemainingSeconds - 1
                _uiState.value = _uiState.value.copy(timeRemainingSeconds = remaining)

                if (remaining <= 0) {
                    finishTest()
                    break
                }
            }
        }
    }

    fun finishTest() {
        timerJob?.cancel()
        val state = _uiState.value
        if (state.isFinished) return

        val targetWords = state.targetText.split(" ")
        val typedWords = state.userInput.split(" ")
        var correctWordsCount = 0
        var incorrectWordsCount = 0

        for (i in typedWords.indices) {
            if (i < targetWords.length && typedWords[i] == targetWords[i]) {
                correctWordsCount++
            } else {
                incorrectWordsCount++
            }
        }

        _uiState.value = state.copy(
            isRunning = false,
            isFinished = true,
            correctWords = correctWordsCount,
            incorrectWords = incorrectWordsCount,
            totalWords = typedWords.size
        )

        // Save result to Room DB if not in practice mode
        if (!state.isPracticeMode) {
            viewModelScope.launch {
                val result = TypingResult(
                    wpm = state.currentWpm,
                    accuracy = state.accuracy,
                    totalWords = typedWords.size,
                    correctWords = correctWordsCount,
                    incorrectWords = incorrectWordsCount,
                    totalMistakes = state.totalMistakes,
                    totalCharacters = state.userInput.length,
                    durationSeconds = state.durationSeconds - state.timeRemainingSeconds,
                    difficulty = state.difficulty.name
                )
                val id = dao.insertResult(result)
                _uiState.value = _uiState.value.copy(lastSavedResultId = id)
            }
        }
    }

    fun resetTest() {
        timerJob?.cancel()
        startTest(_uiState.value.durationSeconds, _uiState.value.difficulty, _uiState.value.isPracticeMode)
    }
}

package com.typingarena.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.typingarena.ui.screens.*
import com.typingarena.ui.viewmodel.*

object Routes {
    const val HOME = "home"
    const val TYPING_TEST = "typing_test"
    const val RESULTS = "results"
    const val PRACTICE = "practice"
    const val STATISTICS = "statistics"
    const val ACHIEVEMENTS = "achievements"
    const val SETTINGS = "settings"
    const val DAILY_CHALLENGE = "daily_challenge"
}

@Composable
fun TypingArenaNavGraph(
    navController: NavHostController,
    typingEngineViewModel: TypingEngineViewModel = viewModel(),
    statsViewModel: StatsViewModel = viewModel(),
    achievementsViewModel: AchievementsViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by typingEngineViewModel.uiState.collectAsState()
    val highestWpm by statsViewModel.highestWpm.collectAsState()
    val averageWpm by statsViewModel.averageWpm.collectAsState()
    val averageAccuracy by statsViewModel.averageAccuracy.collectAsState()
    val totalTests by statsViewModel.totalTests.collectAsState()
    val totalTimeSeconds by statsViewModel.totalTimeSeconds.collectAsState()
    val resultsHistory by statsViewModel.allResults.collectAsState()
    val achievements by achievementsViewModel.achievements.collectAsState()
    val userSettings by settingsViewModel.userSettings.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                bestWpm = highestWpm ?: 0,
                onNavigateToTest = {
                    typingEngineViewModel.startTest(
                        durationSeconds = 30,
                        difficulty = userSettings.defaultDifficulty,
                        isPractice = false
                    )
                    navController.navigate(Routes.TYPING_TEST)
                },
                onNavigateToPractice = {
                    typingEngineViewModel.startTest(
                        durationSeconds = 0,
                        difficulty = userSettings.defaultDifficulty,
                        isPractice = true
                    )
                    navController.navigate(Routes.PRACTICE)
                },
                onNavigateToStats = { navController.navigate(Routes.STATISTICS) },
                onNavigateToAchievements = { navController.navigate(Routes.ACHIEVEMENTS) },
                onNavigateToDailyChallenge = { navController.navigate(Routes.DAILY_CHALLENGE) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.TYPING_TEST) {
            TypingTestScreen(
                uiState = uiState,
                onInputTyped = { input -> typingEngineViewModel.onInputTyped(input) },
                onStartTest = { duration, difficulty -> typingEngineViewModel.startTest(duration, difficulty, false) },
                onResetTest = { typingEngineViewModel.resetTest() },
                onNavigateToResults = { navController.navigate(Routes.RESULTS) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.RESULTS) {
            ResultsScreen(
                uiState = uiState,
                onRetry = {
                    typingEngineViewModel.resetTest()
                    navController.navigate(Routes.TYPING_TEST) {
                        popUpTo(Routes.HOME)
                    }
                },
                onReturnHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PRACTICE) {
            PracticeScreen(
                uiState = uiState,
                onInputTyped = { input -> typingEngineViewModel.onInputTyped(input) },
                onResetTest = { typingEngineViewModel.resetTest() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STATISTICS) {
            StatisticsScreen(
                highestWpm = highestWpm ?: 0,
                averageWpm = averageWpm ?: 0f,
                averageAccuracy = averageAccuracy ?: 0f,
                totalTests = totalTests,
                totalTimeSeconds = totalTimeSeconds ?: 0L,
                resultsHistory = resultsHistory,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ACHIEVEMENTS) {
            AchievementsScreen(
                achievements = achievements,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.DAILY_CHALLENGE) {
            DailyChallengeScreen(
                onStartChallenge = {
                    typingEngineViewModel.startTest(
                        durationSeconds = 60,
                        difficulty = userSettings.defaultDifficulty,
                        isPractice = false
                    )
                    navController.navigate(Routes.TYPING_TEST)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                userSettings = userSettings,
                onThemeChanged = { mode -> settingsViewModel.setThemeMode(mode) },
                onFontSizeChanged = { size -> settingsViewModel.setFontSize(size) },
                onSoundToggled = { enabled -> settingsViewModel.setSoundEnabled(enabled) },
                onVibrationToggled = { enabled -> settingsViewModel.setVibrationEnabled(enabled) },
                onDifficultyChanged = { diff -> settingsViewModel.setDefaultDifficulty(diff) },
                onClearStats = { statsViewModel.clearStatistics() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

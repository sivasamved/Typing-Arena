package com.typingarena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.typingarena.ui.navigation.TypingArenaNavGraph
import com.typingarena.ui.theme.TypingArenaTheme
import com.typingarena.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userSettings by settingsViewModel.userSettings.collectAsState()

            TypingArenaTheme(themeMode = userSettings.themeMode) {
                val navController = rememberNavController()
                TypingArenaNavGraph(navController = navController)
            }
        }
    }
}

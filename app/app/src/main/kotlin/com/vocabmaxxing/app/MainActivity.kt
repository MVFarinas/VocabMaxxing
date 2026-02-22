package com.vocabmaxxing.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vocabmaxxing.app.ui.auth.AuthScreen
import com.vocabmaxxing.app.ui.auth.AuthViewModel
import com.vocabmaxxing.app.ui.daily.DailyScreen
import com.vocabmaxxing.app.ui.daily.DailyViewModel
import com.vocabmaxxing.app.ui.dashboard.DashboardScreen
import com.vocabmaxxing.app.ui.dashboard.DashboardViewModel
import com.vocabmaxxing.app.ui.theme.VocabMaxxingTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as VocabMaxxingApp

        setContent {
            VocabMaxxingTheme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return AuthViewModel(app.apiClient, app.tokenManager) as T
                        }
                    }
                )

                val authState by authViewModel.uiState.collectAsState()

                // Navigate on auth state changes
                LaunchedEffect(authState.isAuthenticated) {
                    if (authState.isAuthenticated) {
                        navController.navigate("daily") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = if (authState.isAuthenticated) "daily" else "auth"
                ) {
                    composable("auth") {
                        AuthScreen(
                            onLogin = { email, pw -> authViewModel.login(email, pw) },
                            onRegister = { email, pw -> authViewModel.register(email, pw) },
                            isLoading = authState.isLoading,
                            error = authState.error,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable("daily") {
                        val dailyViewModel: DailyViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    @Suppress("UNCHECKED_CAST")
                                    return DailyViewModel(app.apiClient, app.tokenManager) as T
                                }
                            }
                        )
                        val dailyState by dailyViewModel.uiState.collectAsState()

                        DailyScreen(
                            words = dailyState.words,
                            isLoading = dailyState.isLoading,
                            isSubmitting = dailyState.isSubmitting,
                            error = dailyState.error,
                            result = dailyState.result,
                            onSubmit = { wordId, sentence ->
                                dailyViewModel.submitSentence(wordId, sentence)
                            },
                            onReset = { dailyViewModel.reset() },
                            onNavigateDashboard = {
                                navController.navigate("dashboard")
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable("dashboard") {
                        val dashViewModel: DashboardViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    @Suppress("UNCHECKED_CAST")
                                    return DashboardViewModel(app.apiClient, app.tokenManager) as T
                                }
                            }
                        )
                        val dashState by dashViewModel.uiState.collectAsState()

                        DashboardScreen(
                            data = dashState.data,
                            isLoading = dashState.isLoading,
                            onNavigateDaily = {
                                navController.navigate("daily") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            },
                            onLogout = {
                                authViewModel.logout()
                                navController.navigate("auth") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

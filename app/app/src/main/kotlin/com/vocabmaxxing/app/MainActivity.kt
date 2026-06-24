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
import com.vocabmaxxing.app.ui.auth.SignUpScreen
import com.vocabmaxxing.app.ui.daily.DailyScreen
import com.vocabmaxxing.app.ui.daily.DailyViewModel
import com.vocabmaxxing.app.ui.dashboard.DashboardScreen
import com.vocabmaxxing.app.ui.dashboard.DashboardViewModel
import com.vocabmaxxing.app.ui.theme.VocabMaxxingTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Pre-authentication routes: while on any of these, the session guard must not
// bounce the user back to "auth", and reaching an authed state forwards to "daily".
private val authFlowRoutes = setOf("auth", "signup", "forgot", "verify", "reset")

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

                // Always start at "auth". AuthViewModel verifies the cached
                // session asynchronously; if valid, the LaunchedEffect below
                // forwards to "daily". This avoids landing on an authed screen
                // with a stale token that can't make API calls.
                LaunchedEffect(authState.isAuthenticated) {
                    val current = navController.currentBackStackEntry?.destination?.route
                    val isAuthFlow = current in authFlowRoutes
                    if (authState.isAuthenticated && isAuthFlow) {
                        navController.navigate("daily") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else if (!authState.isAuthenticated && current != null && !isAuthFlow) {
                        navController.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "auth")
                {
                    composable("auth") {
                        AuthScreen(
                            onLogin = { email, pw -> authViewModel.login(email, pw) },
                            onNavigateSignUp = { navController.navigate("signup") },
                            // TODO(Step 2): navigate to "forgot" route once added.
                            onForgotPassword = { },
                            isLoading = authState.isLoading,
                            error = authState.error,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable("signup") {
                        SignUpScreen(
                            onRegister = { email, pw -> authViewModel.register(email, pw) },
                            onNavigateSignIn = { navController.popBackStack() },
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
                            onRetry = { dailyViewModel.loadDailyWords() },
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

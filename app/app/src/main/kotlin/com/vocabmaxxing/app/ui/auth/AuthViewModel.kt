package com.vocabmaxxing.app.ui.auth

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaxxing.app.data.api.ApiClient
import com.vocabmaxxing.app.data.repository.TokenManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.long

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(
    private val apiClient: ApiClient,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Check if we have an existing, non-expired token
        viewModelScope.launch {
            val token = tokenManager.token.first()
            if (token != null && !isTokenExpired(token)) {
                _uiState.value = _uiState.value.copy(isAuthenticated = true)
            } else if (token != null) {
                // Token exists but is expired — clear it so user must log in again
                tokenManager.clearSession()
            }
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING))
            val exp = Json.parseToJsonElement(payload).jsonObject["exp"]?.long ?: return true
            System.currentTimeMillis() / 1000 >= exp
        } catch (e: Exception) {
            true // Treat malformed token as expired
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            apiClient.login(email, password)
                .onSuccess { response ->
                    tokenManager.saveSession(response.token, response.userId, response.email)
                    _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed."
                    )
                }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            apiClient.register(email, password)
                .onSuccess { response ->
                    tokenManager.saveSession(response.token, response.userId, response.email)
                    _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Registration failed."
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearSession()
            _uiState.value = AuthUiState()
        }
    }
}

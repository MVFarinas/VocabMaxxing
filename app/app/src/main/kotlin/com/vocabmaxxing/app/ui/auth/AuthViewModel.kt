package com.vocabmaxxing.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaxxing.app.data.api.ApiClient
import com.vocabmaxxing.app.data.repository.TokenManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
        // Check if we have an existing token
        viewModelScope.launch {
            tokenManager.token.first()?.let {
                _uiState.value = _uiState.value.copy(isAuthenticated = true)
            }
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

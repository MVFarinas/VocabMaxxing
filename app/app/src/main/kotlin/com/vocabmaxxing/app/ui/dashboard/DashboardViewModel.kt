package com.vocabmaxxing.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaxxing.app.data.api.ApiClient
import com.vocabmaxxing.app.data.model.DashboardResponse
import com.vocabmaxxing.app.data.repository.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val data: DashboardResponse? = null,
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val apiClient: ApiClient,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val token = tokenManager.getFreshToken() ?: return@launch

            apiClient.getDashboard(token)
                .onSuccess { response ->
                    _uiState.value = DashboardUiState(data = response, isLoading = false)
                }
                .onFailure {
                    _uiState.value = DashboardUiState(isLoading = false)
                }
        }
    }
}

package com.justappz.aniyomitv.extensions_management.presentation.states

sealed class RepoUiState {

    object Idle : RepoUiState()
    object Loading : RepoUiState()

    data class Success(val data: List<String>) : RepoUiState()

    data class Error(
        val code: Int? = null,   // e.g., HTTP status code, or custom app error code
        val message: String,      // human-readable message
    ) : RepoUiState()
}

package com.justappz.aniyomitv.extensions_management.presentation.states

import com.justappz.aniyomitv.extensions_management.domain.model.AnimeRepositoriesDetailsDomain

sealed class RepoUiState {

    object Idle : RepoUiState()
    object Loading : RepoUiState()

    data class Success(val data: List<AnimeRepositoriesDetailsDomain>) : RepoUiState()

    data class Error(
        val code: Int? = null,   // e.g., HTTP status code, or custom app error code
        val message: String,      // human-readable message
    ) : RepoUiState()
}

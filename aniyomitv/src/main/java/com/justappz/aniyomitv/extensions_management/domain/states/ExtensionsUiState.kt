package com.justappz.aniyomitv.extensions_management.domain.states

import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain

sealed class ExtensionsUiState {
    object Loading : ExtensionsUiState()

    data class Success(val data: List<ExtensionDomain>) : ExtensionsUiState()

    data class Error(
        val code: Int? = null,   // e.g., HTTP status code, or custom app error code
        val message: String,      // human-readable message
    ) : ExtensionsUiState()
}

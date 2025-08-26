package com.justappz.aniyomitv.anime_search.presentation.states

import com.justappz.aniyomitv.anime_search.domain.model.InstalledExtensions

sealed class GetInstalledExtensionsState {

    object Idle : GetInstalledExtensionsState()
    object Loading : GetInstalledExtensionsState()

    data class Success(val installedExtensions: List<InstalledExtensions>) : GetInstalledExtensionsState()

    data class Error(
        val code: Int? = null,   // e.g., HTTP status code, or custom app error code
        val message: String,      // human-readable message
    ) : GetInstalledExtensionsState()
}

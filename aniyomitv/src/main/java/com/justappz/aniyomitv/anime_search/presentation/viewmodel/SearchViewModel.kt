package com.justappz.aniyomitv.anime_search.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.anime_search.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.anime_search.presentation.states.GetInstalledExtensionsState
import com.justappz.aniyomitv.extensions_management.presentation.states.RepoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getInstalledExtensionsUseCase: GetInstalledExtensionsUseCase,
) : ViewModel() {

    //region extensions
    private val _extensionState = MutableStateFlow<GetInstalledExtensionsState>(GetInstalledExtensionsState.Idle)
    val extensionState: StateFlow<GetInstalledExtensionsState> = _extensionState.asStateFlow()

    fun getExtensions(context: Context) {
        viewModelScope.launch {
            _extensionState.value = GetInstalledExtensionsState.Loading
            try {
                val extensions = getInstalledExtensionsUseCase(context)
                _extensionState.value = GetInstalledExtensionsState.Success(extensions)
            } catch (e: Exception) {
                _extensionState.value = GetInstalledExtensionsState.Error(
                    code = null, // you can map exceptions to codes if needed
                    message = e.message ?: "Unexpected error"
                )
            }
        }
    }

    fun resetExtensionState() {
        _extensionState.value = GetInstalledExtensionsState.Idle
    }

    //endregion

}

package com.justappz.aniyomitv.extensions_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.extensions_management.presentation.states.ExtensionsUiState
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import eu.kanade.tachiyomi.network.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ExtensionViewModel(
    private val getExtensionsUseCase: GetExtensionUseCase,
) : ViewModel() {

    private val _extensionState = MutableStateFlow<ExtensionsUiState>(ExtensionsUiState.Idle)
    val extensionState: StateFlow<ExtensionsUiState> = _extensionState.asStateFlow()

    fun loadExtensions(repoUrl: String) {
        viewModelScope.launch {
            _extensionState.value = ExtensionsUiState.Loading
            try {
                val list = withContext(Dispatchers.IO) {
                    getExtensionsUseCase(repoUrl)
                }
                _extensionState.value = ExtensionsUiState.Success(list)
            } catch (e: HttpException) {
                _extensionState.value = ExtensionsUiState.Error(
                    code = e.code,
                    message = e.message ?: "Unexpected HTTP error",
                )
            } catch (e: IOException) {
                _extensionState.value = ExtensionsUiState.Error(
                    code = null,
                    message = "Network error, check your connection",
                )
            } catch (e: Exception) {
                _extensionState.value = ExtensionsUiState.Error(
                    code = null,
                    message = e.localizedMessage ?: "Unknown error",
                )
            }
        }
    }
}

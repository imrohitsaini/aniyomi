package com.justappz.aniyomitv.search.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.base.BaseUiState.Idle
import com.justappz.aniyomitv.base.BaseUiState.Loading
import com.justappz.aniyomitv.discover.domain.model.InstalledExtensions
import com.justappz.aniyomitv.discover.domain.usecase.GetInstalledExtensionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getInstalledExtensionsUseCase: GetInstalledExtensionsUseCase,
) : ViewModel() {

    //region extensions
    // Extensions (reusing BaseUiState instead of GetInstalledExtensionsState)
    private val _extensionState = MutableStateFlow<BaseUiState<List<InstalledExtensions>>>(Idle)
    val extensionState: StateFlow<BaseUiState<List<InstalledExtensions>>> = _extensionState.asStateFlow()

    fun getExtensions(context: Context) {
        viewModelScope.launch {
            _extensionState.value = Loading
            _extensionState.value = getInstalledExtensionsUseCase(context)
        }
    }

    fun resetExtensionState() {
        _extensionState.value = Idle
    }

    //endregion
}

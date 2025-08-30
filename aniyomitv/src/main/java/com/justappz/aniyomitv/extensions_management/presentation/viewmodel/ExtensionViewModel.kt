package com.justappz.aniyomitv.extensions_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.model.RepoDomain
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionRepoDetailsUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.InsertExtensionRepoUrlUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExtensionViewModel(
    private val getExtensionsUseCase: GetExtensionUseCase,
    private val getRepoUrlsUseCase: GetExtensionRepoDetailsUseCase,
    private val saveRepoUrlUseCase: InsertExtensionRepoUrlUseCase,
) : ViewModel() {

    //region repo
    //region loadRepoUrls
    private val _repoUrls = MutableStateFlow<BaseUiState<List<ExtensionRepositoriesDetailsDomain>>>(BaseUiState.Idle)
    val repoUrls: StateFlow<BaseUiState<List<ExtensionRepositoriesDetailsDomain>>> = _repoUrls.asStateFlow()
    fun loadRepoUrls() {
        viewModelScope.launch {
            _repoUrls.value = BaseUiState.Loading
            val uiState = withContext(Dispatchers.IO) {
                getRepoUrlsUseCase()
            }
            _repoUrls.value = uiState
        }
    }
    //endregion

    fun addRepo(url: String) {
        viewModelScope.launch {
            val result = saveRepoUrlUseCase(url)
            when (result) {
                is BaseUiState.Success -> {
                    loadRepoUrls()
                }

                else -> {}
            }
        }
    }

    fun resetRepoState() {
        _repoUrls.value = BaseUiState.Idle
    }
    //endregion

    //region extensions
    private val _extensionState = MutableStateFlow<BaseUiState<RepoDomain>>(BaseUiState.Idle)
    val extensionState: StateFlow<BaseUiState<RepoDomain>> = _extensionState.asStateFlow()

    fun loadExtensions(repoUrl: String) {
        viewModelScope.launch {
            _extensionState.value = BaseUiState.Loading
            val uiState = withContext(Dispatchers.IO) {
                getExtensionsUseCase(repoUrl)
            }
            _extensionState.value = uiState
        }
    }

    fun resetExtensionState() {
        _extensionState.value = BaseUiState.Idle
    }
    //endregion
}

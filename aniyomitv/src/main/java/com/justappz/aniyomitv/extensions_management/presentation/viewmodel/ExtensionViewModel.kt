package com.justappz.aniyomitv.extensions_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.util.UrlUtils
import com.justappz.aniyomitv.extensions_management.domain.model.AnimeRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.model.RepoDomain
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetRepoUrlsUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.SaveRepoUrlUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExtensionViewModel(
    private val getExtensionsUseCase: GetExtensionUseCase,
    private val getRepoUrlsUseCase: GetRepoUrlsUseCase,
    private val saveRepoUrlUseCase: SaveRepoUrlUseCase,
) : ViewModel() {

    //region repo
    //region loadRepoUrls
    private val _repoUrls = MutableStateFlow<BaseUiState<List<AnimeRepositoriesDetailsDomain>>>(BaseUiState.Idle)
    val repoUrls: StateFlow<BaseUiState<List<AnimeRepositoriesDetailsDomain>>> = _repoUrls.asStateFlow()
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
        val animeRepositoriesDetailsDomain = AnimeRepositoriesDetailsDomain(
            repoUrl = url,
            cleanName = UrlUtils.getCleanUrl(url),
            dateAdded = System.currentTimeMillis(),
        )

        saveRepoUrlUseCase(animeRepositoriesDetailsDomain)
        loadRepoUrls()
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

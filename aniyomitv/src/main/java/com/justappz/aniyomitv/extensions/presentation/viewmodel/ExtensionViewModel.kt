package com.justappz.aniyomitv.extensions.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.extensions.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions.domain.model.RepoDomain
import com.justappz.aniyomitv.extensions.domain.usecase.GetExtensionRepoDetailsUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.InsertExtensionRepoUrlUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.ObserveExtensionsUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.RefreshExtensionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExtensionViewModel(
    private val getRepoUrlsUseCase: GetExtensionRepoDetailsUseCase,
    private val saveRepoUrlUseCase: InsertExtensionRepoUrlUseCase,
    private val observeExtensionsUseCase: ObserveExtensionsUseCase,
    private val refreshExtensionsUseCase: RefreshExtensionsUseCase,
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

                else -> {
                    _repoUrls.value = BaseUiState.Error(
                        AppError.RoomDbError(
                            message = "Unable to save repo",
                            displayType = ErrorDisplayType.TOAST,
                        ),
                    )
                }
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
        // Observe DB
        viewModelScope.launch {
            observeExtensionsUseCase(repoUrl).collect { state ->
                _extensionState.value = state
            }
        }

        // Trigger network refresh
        viewModelScope.launch(Dispatchers.IO) {
            refreshExtensionsUseCase(repoUrl)
        }
    }

    fun loadExtensionsFromNewUrl(repoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Step 1: Fetch from network first
            _extensionState.value = BaseUiState.Idle
            val apiResult = refreshExtensionsUseCase(repoUrl)

            when (apiResult) {
                is BaseUiState.Success -> {
                    // Step 2: Start observing DB for updated extensions
                    observeExtensionsUseCase(repoUrl).collect { state ->
                        _extensionState.value = state
                    }
                }

                else -> {
                    _extensionState.value = BaseUiState.Error(
                        AppError.RoomDbError(
                            message = "Unable to get extensions",
                            displayType = ErrorDisplayType.TOAST,
                        ),
                    )
                }
            }
        }
    }

    fun resetExtensionState() {
        _extensionState.value = BaseUiState.Idle
    }
    //endregion
}

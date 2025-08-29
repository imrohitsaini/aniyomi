package com.justappz.aniyomitv.extensions_management.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.util.UrlUtils
import com.justappz.aniyomitv.core.util.toJson
import com.justappz.aniyomitv.extensions_management.domain.model.AnimeRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetRepoUrlsUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.RemoveRepoUrlUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.SaveRepoUrlUseCase
import com.justappz.aniyomitv.extensions_management.presentation.states.ExtensionsUiState
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
    private val getRepoUrlsUseCase: GetRepoUrlsUseCase,
    private val saveRepoUrlUseCase: SaveRepoUrlUseCase,
    private val removeRepoUrlUseCase: RemoveRepoUrlUseCase,
) : ViewModel() {

    private val tag = "ExtensionViewModel"


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
    private val _extensionState = MutableStateFlow<ExtensionsUiState>(ExtensionsUiState.Idle)
    val extensionState: StateFlow<ExtensionsUiState> = _extensionState.asStateFlow()

    fun loadExtensions(repoUrl: String) {
        viewModelScope.launch {
            _extensionState.value = ExtensionsUiState.Loading
            try {
                Log.d(tag, "loadExtensions $repoUrl")
                val repoDomain = withContext(Dispatchers.IO) {
                    getExtensionsUseCase(repoUrl)
                }
                _extensionState.value = ExtensionsUiState.Success(repoDomain)
            } catch (e: HttpException) {
                Log.d(tag, "loadExtensions ${e.toJson()}")
                _extensionState.value = ExtensionsUiState.Error(
                    code = e.code,
                    message = e.message ?: "Unexpected HTTP error",
                )
            } catch (e: IOException) {
                Log.d(tag, "loadExtensions ${e.toJson()}")
                e.printStackTrace()
                _extensionState.value = ExtensionsUiState.Error(
                    code = null,
                    message = "Network error, check your connection",
                )
            } catch (e: Exception) {
                Log.d(tag, "loadExtensions ${e.toJson()}")
                _extensionState.value = ExtensionsUiState.Error(
                    code = null,
                    message = e.localizedMessage ?: "Unknown error",
                )
            }
        }
    }
    //endregion
}

package com.justappz.aniyomitv.anime_search.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.justappz.aniyomitv.anime_search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.anime_search.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.anime_search.domain.usecase.GetLatestAnimePagingUseCase
import com.justappz.aniyomitv.anime_search.domain.usecase.GetPopularAnimePagingUseCase
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.base.BaseUiState.Idle
import com.justappz.aniyomitv.base.BaseUiState.Loading
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getInstalledExtensionsUseCase: GetInstalledExtensionsUseCase,
    private val getPopularAnimePagingUseCase: GetPopularAnimePagingUseCase,
    private val getLatestAnimePagingUseCase: GetLatestAnimePagingUseCase,
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

    //region anime
    fun getPopularAnime(source: AnimeHttpSource): Flow<PagingData<SAnime>> {
        return getPopularAnimePagingUseCase(source).flow.cachedIn(viewModelScope)
    }

    fun getLatestAnime(source: AnimeHttpSource): Flow<PagingData<SAnime>> {
        return getLatestAnimePagingUseCase(source).flow.cachedIn(viewModelScope)
    }
    //endregion

}

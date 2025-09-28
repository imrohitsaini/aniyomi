package com.justappz.aniyomitv.search.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.base.BaseUiState.Idle
import com.justappz.aniyomitv.base.BaseUiState.Loading
import com.justappz.aniyomitv.discover.domain.model.InstalledExtensions
import com.justappz.aniyomitv.discover.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.discover.domain.usecase.SearchAnimePagingUseCase
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getInstalledExtensionsUseCase: GetInstalledExtensionsUseCase,
    private val searchAnimePagingUseCase: SearchAnimePagingUseCase,
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
    fun searchAnime(source: AnimeHttpSource, query: String): Flow<PagingData<SAnime>> {
        return searchAnimePagingUseCase(source, query, AnimeFilterList()).flow.cachedIn(viewModelScope)
    }
    //endregion
}

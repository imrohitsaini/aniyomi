package com.justappz.aniyomitv.anime_search.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.justappz.aniyomitv.anime_search.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.anime_search.domain.usecase.GetLatestAnimePagingUseCase
import com.justappz.aniyomitv.anime_search.domain.usecase.GetPopularAnimePagingUseCase
import com.justappz.aniyomitv.anime_search.presentation.states.GetInstalledExtensionsState
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
                    message = e.message ?: "Unexpected error",
                )
            }
        }
    }

    fun resetExtensionState() {
        _extensionState.value = GetInstalledExtensionsState.Idle
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

package com.justappz.aniyomitv.episodes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.base.BaseUiState.Idle
import com.justappz.aniyomitv.episodes.domain.usecase.GetAnimeDetailsUseCase
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.SAnime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EpisodesViewModel(
    private val getAnimeDetailsUseCase: GetAnimeDetailsUseCase,
) : ViewModel() {

    //region Anime Details
    private val _animeDetailsState = MutableStateFlow<BaseUiState<SAnime>>(Idle)
    val animeDetailsState: StateFlow<BaseUiState<SAnime>> = _animeDetailsState.asStateFlow()

    fun getAnimeDetails(source: AnimeCatalogueSource, anime: SAnime) {
        viewModelScope.launch {
            _animeDetailsState.value = BaseUiState.Loading

            val result = getAnimeDetailsUseCase(source, anime)
            _animeDetailsState.value = result
        }
    }
    //endregion
}

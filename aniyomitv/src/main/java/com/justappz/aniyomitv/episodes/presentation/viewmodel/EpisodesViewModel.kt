package com.justappz.aniyomitv.episodes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.base.BaseUiState.Idle
import com.justappz.aniyomitv.episodes.domain.usecase.GetAnimeDetailsUseCase
import com.justappz.aniyomitv.episodes.domain.usecase.GetEpisodesUseCase
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EpisodesViewModel(
    private val getAnimeDetailsUseCase: GetAnimeDetailsUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
) : ViewModel() {

    //region Anime Details
    private val _animeDetailsState = MutableStateFlow<BaseUiState<SAnime>>(Idle)
    val animeDetailsState: StateFlow<BaseUiState<SAnime>> = _animeDetailsState.asStateFlow()

    fun getAnimeDetails(source: AnimeHttpSource, anime: SAnime) {
        viewModelScope.launch {
            _animeDetailsState.value = BaseUiState.Loading
            _animeDetailsState.value = getAnimeDetailsUseCase(source, anime)
        }
    }
    //endregion

    //region episodes
    private val _episodesList = MutableStateFlow<BaseUiState<List<SEpisode>>>(Idle)
    val episodesList: StateFlow<BaseUiState<List<SEpisode>>> = _episodesList.asStateFlow()

    fun getEpisodesList(source: AnimeHttpSource, anime: SAnime) {
        viewModelScope.launch {
            _episodesList.value = BaseUiState.Loading
            _episodesList.value = getEpisodesUseCase(source, anime)
        }
    }
    //endregion
}

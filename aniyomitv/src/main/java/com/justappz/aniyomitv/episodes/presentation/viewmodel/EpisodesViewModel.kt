package com.justappz.aniyomitv.episodes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.base.BaseUiState.Idle
import com.justappz.aniyomitv.episodes.domain.usecase.GetAnimeDetailsUseCase
import com.justappz.aniyomitv.episodes.domain.usecase.GetEpisodesUseCase
import com.justappz.aniyomitv.episodes.domain.usecase.GetVideosUseCase
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import com.justappz.aniyomitv.playback.domain.usecase.UpdateAnimeWithDbUseCase
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EpisodesViewModel(
    private val getAnimeDetailsUseCase: GetAnimeDetailsUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val getVideosUseCase: GetVideosUseCase,
    private val updateAnimeWithDbUseCase: UpdateAnimeWithDbUseCase,
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
    private val _episodesList = MutableStateFlow<BaseUiState<List<EpisodeDomain>>>(Idle)
    val episodesList: StateFlow<BaseUiState<List<EpisodeDomain>>> = _episodesList.asStateFlow()

    fun getEpisodesList(source: AnimeHttpSource, anime: SAnime) {
        viewModelScope.launch {
            _episodesList.value = BaseUiState.Loading
            _episodesList.value = getEpisodesUseCase(source, anime)
        }
    }

    private val _videosList = MutableStateFlow<BaseUiState<List<Video>>>(Idle)
    val videosList: StateFlow<BaseUiState<List<Video>>> = _videosList.asStateFlow()

    fun getVideosList(source: AnimeHttpSource, episode: SEpisode) {
        viewModelScope.launch {
            _videosList.value = BaseUiState.Loading
            _videosList.value = getVideosUseCase(source, episode)
        }
    }

    fun resetVideoState() {
        _videosList.value = BaseUiState.Idle
    }
    //endregion

    //region add remove to library
    private val _animeDomain = MutableStateFlow<BaseUiState<AnimeDomain>>(Idle)
    val animeDomain: StateFlow<BaseUiState<AnimeDomain>> = _animeDomain.asStateFlow()

    fun updateAnimeWithDb(packageName: String, className: String, anime: SAnime, inLibrary: Boolean) {
        viewModelScope.launch {
            _animeDomain.value = BaseUiState.Loading
            _animeDomain.value = updateAnimeWithDbUseCase(packageName, className, anime, inLibrary)
        }
    }
    //endregion
}

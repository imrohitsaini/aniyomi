package com.justappz.aniyomitv.playback.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.base.BaseUiState.Idle
import com.justappz.aniyomitv.database.usecase.GetAllEpisodesForAnime
import com.justappz.aniyomitv.database.usecase.UpdateEpisodeWithDbUseCase
import com.justappz.aniyomitv.episodes.domain.usecase.GetVideosUseCase
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val updateEpisodeWithDbUseCase: UpdateEpisodeWithDbUseCase,
    private val getAllEpisodesForAnime: GetAllEpisodesForAnime,
    private val getVideosUseCase: GetVideosUseCase,
) : ViewModel() {

    //region update episode with db
    private val _episodeDomain = MutableStateFlow<BaseUiState<EpisodeDomain>>(Idle)
    val episodeDomain: StateFlow<BaseUiState<EpisodeDomain>> = _episodeDomain.asStateFlow()

    fun updateEpisodeWithDb(
        animeUrl: String,
        lastWatchTime: Long,
        watchState: Int,
        episode: SEpisode,
    ) {
        viewModelScope.launch {
            _episodeDomain.value = BaseUiState.Loading
            _episodeDomain.value = updateEpisodeWithDbUseCase(animeUrl, lastWatchTime, watchState, episode)
        }
    }
    //endregion

    //region Get all episodes
    private val _episodesDomain = MutableStateFlow<BaseUiState<List<EpisodeDomain>>>(Idle)
    val episodesDomain: StateFlow<BaseUiState<List<EpisodeDomain>>> = _episodesDomain.asStateFlow()

    fun getAllEpisodes(
        animeUrl: String,
    ) {
        viewModelScope.launch {
            _episodesDomain.value = BaseUiState.Loading
            _episodesDomain.value = getAllEpisodesForAnime(animeUrl)
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
        _videosList.value = Idle
    }
    //endregion
}

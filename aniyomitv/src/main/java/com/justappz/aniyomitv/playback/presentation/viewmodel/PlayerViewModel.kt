package com.justappz.aniyomitv.playback.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import com.justappz.aniyomitv.playback.domain.usecase.UpdateAnimeWithDbUseCase
import com.justappz.aniyomitv.playback.domain.usecase.UpdateEpisodeWithDbUseCase
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val updateAnimeWithDbUseCase: UpdateAnimeWithDbUseCase,
    private val updateEpisodeWithDbUseCase: UpdateEpisodeWithDbUseCase,
) : ViewModel() {

    //region get anime from db
    private val _animeDomain = MutableStateFlow<BaseUiState<AnimeDomain>>(BaseUiState.Idle)
    val animeDomain: StateFlow<BaseUiState<AnimeDomain>> = _animeDomain.asStateFlow()

    fun updateAnimeWithDb(packageName: String, className: String, anime: SAnime) {
        viewModelScope.launch {
            _animeDomain.value = BaseUiState.Loading
            _animeDomain.value = updateAnimeWithDbUseCase(packageName, className, anime)
        }
    }
    //endregion

    //region update episode with db
    private val _episodeDomain = MutableStateFlow<BaseUiState<EpisodeDomain>>(BaseUiState.Idle)
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
}

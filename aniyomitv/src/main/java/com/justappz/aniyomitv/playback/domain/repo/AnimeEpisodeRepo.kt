package com.justappz.aniyomitv.playback.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain

interface AnimeEpisodeRepo {
    // updates
    suspend fun updateAnimeWithDb(animeDomain: AnimeDomain): BaseUiState<AnimeDomain>
    suspend fun updateEpisodeWithDb(episode: EpisodeDomain): BaseUiState<EpisodeDomain>

    // fetch
    suspend fun getAnimeWithKey(animeKey: String): BaseUiState<AnimeDomain?>
    suspend fun getAnimeInLibrary(): BaseUiState<List<AnimeDomain>>
    suspend fun getAllEpisodesForAnime(animeKey: String): BaseUiState<List<EpisodeDomain>>


}

package com.justappz.aniyomitv.playback.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import eu.kanade.tachiyomi.animesource.model.SEpisode

interface AnimeEpisodeRepo {
    suspend fun updateAnimeWithDb(animeDomain: AnimeDomain): BaseUiState<AnimeDomain>
    suspend fun updateEpisodeWithDb(episode: EpisodeDomain): BaseUiState<EpisodeDomain>

}

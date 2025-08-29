package com.justappz.aniyomitv.episodes.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

interface EpisodesRepository {
    suspend fun getAnimeDetails(source: AnimeHttpSource, anime: SAnime): BaseUiState<SAnime>
    suspend fun getEpisodes(source: AnimeHttpSource, anime: SAnime): BaseUiState<List<SEpisode>>
}

package com.justappz.aniyomitv.episodes.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

interface EpisodesRepository {
    suspend fun getAnimeDetails(source: AnimeHttpSource, anime: SAnime): BaseUiState<SAnime>
    suspend fun getEpisodes(source: AnimeHttpSource, anime: SAnime): BaseUiState<List<SEpisode>>
    suspend fun getVideos(source: AnimeHttpSource, episode: SEpisode): BaseUiState<List<Video>>
}

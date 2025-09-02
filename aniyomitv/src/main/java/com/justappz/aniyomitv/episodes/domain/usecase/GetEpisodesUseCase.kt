package com.justappz.aniyomitv.episodes.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.episodes.domain.repo.EpisodesRepository
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

class GetEpisodesUseCase(
    private val repo: EpisodesRepository,
) {
    suspend operator fun invoke(source: AnimeHttpSource, anime: SAnime): BaseUiState<List<EpisodeDomain>> {
        return repo.getEpisodes(source, anime)
    }
}

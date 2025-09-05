package com.justappz.aniyomitv.database.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.util.StringUtils.getAnimeKeyFromUrl
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo

class GetAllEpisodesForAnime(
    private val animeEpisodeRepo: AnimeEpisodeRepo,
) {
    suspend operator fun invoke(
        animeUrl: String,
    ): BaseUiState<List<EpisodeDomain>> {
        return animeEpisodeRepo.getAllEpisodesForAnime(animeUrl.getAnimeKeyFromUrl())
    }
}

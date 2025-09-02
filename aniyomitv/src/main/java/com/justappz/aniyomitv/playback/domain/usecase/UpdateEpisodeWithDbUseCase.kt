package com.justappz.aniyomitv.playback.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import com.justappz.aniyomitv.playback.domain.model.getEpisodeDomain
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo
import eu.kanade.tachiyomi.animesource.model.SEpisode

class UpdateEpisodeWithDbUseCase(
    private val animeEpisodeRepo: AnimeEpisodeRepo,
) {
    suspend operator fun invoke(
        animeUrl: String,
        lastWatchTime: Long,
        watchState: Int,
        episode: SEpisode,
    ): BaseUiState<EpisodeDomain> {
        val episodeDomain = getEpisodeDomain(animeUrl, lastWatchTime, watchState, episode)
        return animeEpisodeRepo.updateEpisodeWithDb(episodeDomain)
    }
}

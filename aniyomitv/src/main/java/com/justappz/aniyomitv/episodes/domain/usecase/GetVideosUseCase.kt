package com.justappz.aniyomitv.episodes.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.episodes.domain.repo.EpisodesRepository
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

class GetVideosUseCase(
    private val repo: EpisodesRepository,
) {
    suspend operator fun invoke(source: AnimeHttpSource, episode: SEpisode): BaseUiState<List<Video>> {
        return repo.getVideos(source, episode)
    }
}

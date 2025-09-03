package com.justappz.aniyomitv.library.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo

class GetAnimeInLibraryUseCase(
    private val animeEpisodeRepo: AnimeEpisodeRepo,
) {
    suspend operator fun invoke(): BaseUiState<List<AnimeDomain>> {
        return animeEpisodeRepo.getAnimeInLibrary()
    }
}

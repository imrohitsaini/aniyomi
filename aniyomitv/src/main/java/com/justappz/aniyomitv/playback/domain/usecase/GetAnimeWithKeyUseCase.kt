package com.justappz.aniyomitv.playback.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.util.StringUtils.getAnimeKeyFromUrl
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo

class GetAnimeWithKeyUseCase(
    private val animeEpisodeRepo: AnimeEpisodeRepo,
) {
    suspend operator fun invoke(url: String): BaseUiState<AnimeDomain?> {
        val animeKey = url.getAnimeKeyFromUrl()
        return animeEpisodeRepo.getAnimeWithKey(animeKey)
    }
}

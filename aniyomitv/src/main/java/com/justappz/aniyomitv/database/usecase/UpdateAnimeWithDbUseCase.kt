package com.justappz.aniyomitv.database.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.getAnimeDomain
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo
import eu.kanade.tachiyomi.animesource.model.SAnime

class UpdateAnimeWithDbUseCase(
    private val animeEpisodeRepo: AnimeEpisodeRepo,
) {
    suspend operator fun invoke(
        packageName: String,
        className: String,
        anime: SAnime,
        inLibrary: Boolean? = false,
    ): BaseUiState<AnimeDomain> {
        val animeDomain = getAnimeDomain(packageName, className, anime, inLibrary)
        return animeEpisodeRepo.updateAnimeWithDb(animeDomain)
    }
}

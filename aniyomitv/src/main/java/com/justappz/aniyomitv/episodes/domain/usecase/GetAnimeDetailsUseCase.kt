package com.justappz.aniyomitv.episodes.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.episodes.domain.repo.EpisodesRepository
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.SAnime

class GetAnimeDetailsUseCase(
    private val repo: EpisodesRepository,
) {
    suspend operator fun invoke(source: AnimeCatalogueSource, anime: SAnime): BaseUiState<SAnime> {
        return repo.getAnimeDetails(source, anime)
    }
}

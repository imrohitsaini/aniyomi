package com.justappz.aniyomitv.anime_search.domain.usecase

import androidx.paging.Pager
import com.justappz.aniyomitv.anime_search.domain.repo.AnimeRepository
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.SAnime

class GetLatestAnimePagingUseCase(
    private val repo: AnimeRepository,
) {
    operator fun invoke(source: AnimeCatalogueSource): Pager<Long, SAnime> {
        return repo.getLatestAnimePaging(source)
    }
}

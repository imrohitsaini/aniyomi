package com.justappz.aniyomitv.discover.domain.usecase

import androidx.paging.Pager
import com.justappz.aniyomitv.discover.domain.repo.AnimeRepository
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.SAnime

class SearchAnimePagingUseCase(
    private val repo: AnimeRepository,
) {
    operator fun invoke(
        source: AnimeCatalogueSource,
        query: String,
        filters: AnimeFilterList,
    ): Pager<Long, SAnime> {
        return repo.getSearchAnimePaging(source, query, filters)
    }
}

package com.justappz.aniyomitv.discover.domain.repo

import androidx.paging.Pager
import com.justappz.aniyomitv.base.BaseUiState
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

interface AnimeRepository {
    fun getPopularAnimePaging(source: AnimeCatalogueSource): Pager<Long, SAnime>
    fun getLatestAnimePaging(source: AnimeCatalogueSource): Pager<Long, SAnime>

    fun getSearchAnimePaging(
        source: AnimeCatalogueSource,
        query: String,
        filters: AnimeFilterList
    ): Pager<Long, SAnime>
}

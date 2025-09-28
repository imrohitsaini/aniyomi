package com.justappz.aniyomitv.discover.domain.repo

import androidx.paging.Pager
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.SAnime

interface AnimeRepository {
    fun getPopularAnimePaging(source: AnimeCatalogueSource): Pager<Long, SAnime>
    fun getLatestAnimePaging(source: AnimeCatalogueSource): Pager<Long, SAnime>
}

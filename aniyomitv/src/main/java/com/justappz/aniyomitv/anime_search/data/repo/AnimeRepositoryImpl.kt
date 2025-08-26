package com.justappz.aniyomitv.anime_search.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.justappz.aniyomitv.anime_search.domain.repo.AnimeRepository
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.SAnime
import tachiyomi.data.source.anime.AnimeSourceLatestPagingSource
import tachiyomi.data.source.anime.AnimeSourcePopularPagingSource

class AnimeRepositoryImpl : AnimeRepository {

    override fun getPopularAnimePaging(source: AnimeCatalogueSource): Pager<Long, SAnime> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { AnimeSourcePopularPagingSource(source) },
        )
    }

    override fun getLatestAnimePaging(source: AnimeCatalogueSource): Pager<Long, SAnime> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { AnimeSourceLatestPagingSource(source) },
        )
    }


}

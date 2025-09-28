package com.justappz.aniyomitv.discover.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.discover.domain.repo.AnimeRepository
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tachiyomi.data.source.anime.AnimeSourceLatestPagingSource
import tachiyomi.data.source.anime.AnimeSourcePopularPagingSource
import tachiyomi.data.source.anime.AnimeSourceSearchPagingSource
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

    override fun getSearchAnimePaging(
        source: AnimeCatalogueSource,
        query: String,
        filters: AnimeFilterList
    ): Pager<Long, SAnime> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { AnimeSourceSearchPagingSource(source, query, filters) },
        )
    }


}

package com.justappz.aniyomitv.episodes.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.SAnime

interface EpisodesRepository {
    suspend fun getAnimeDetails(source: AnimeCatalogueSource, anime: SAnime): BaseUiState<SAnime>
}

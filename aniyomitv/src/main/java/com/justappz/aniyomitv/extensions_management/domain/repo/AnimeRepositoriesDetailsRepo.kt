package com.justappz.aniyomitv.extensions_management.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions_management.domain.model.AnimeRepositoriesDetailsDomain

interface AnimeRepositoriesDetailsRepo {
    fun getRepos(): BaseUiState<List<AnimeRepositoriesDetailsDomain>>
    fun addRepo(animeRepoDetail: AnimeRepositoriesDetailsDomain)
    fun removeRepo(animeRepoDetail: AnimeRepositoriesDetailsDomain)
    fun clearRepos()
}

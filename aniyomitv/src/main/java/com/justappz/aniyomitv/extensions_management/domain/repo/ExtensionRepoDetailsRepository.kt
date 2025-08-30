package com.justappz.aniyomitv.extensions_management.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionRepositoriesDetailsDomain

interface ExtensionRepoDetailsRepository {
    suspend fun getRepos(): BaseUiState<List<ExtensionRepositoriesDetailsDomain>>
    suspend fun addRepo(animeRepoDetail: ExtensionRepositoriesDetailsDomain): BaseUiState<Unit>
}

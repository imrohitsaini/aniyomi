package com.justappz.aniyomitv.extensions.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions.domain.model.ExtensionRepositoriesDetailsDomain

interface ExtensionRepoDetailsRepository {
    suspend fun getRepos(): BaseUiState<List<ExtensionRepositoriesDetailsDomain>>
    suspend fun addRepo(animeRepoDetail: ExtensionRepositoriesDetailsDomain): BaseUiState<Unit>
}

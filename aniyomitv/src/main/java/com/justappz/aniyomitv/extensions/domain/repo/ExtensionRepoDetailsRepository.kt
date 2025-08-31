package com.justappz.aniyomitv.extensions.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions.domain.model.RepoDomain
import kotlinx.coroutines.flow.Flow

interface ExtensionRepoDetailsRepository {
    //repos
    suspend fun getRepos(): BaseUiState<List<ExtensionRepositoriesDetailsDomain>>
    suspend fun addRepo(animeRepoDetail: ExtensionRepositoriesDetailsDomain): BaseUiState<Unit>

    //extensions
    suspend fun loadExtensionsFromDB(url: String): Flow<BaseUiState<RepoDomain>>
    suspend fun refreshExtensions(repoUrl: String): BaseUiState<Unit>
}

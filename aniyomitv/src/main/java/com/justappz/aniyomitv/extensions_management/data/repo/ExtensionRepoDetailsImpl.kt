package com.justappz.aniyomitv.extensions_management.data.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.extensions_management.data.local.dao.ExtensionRepoDetailsDao
import com.justappz.aniyomitv.extensions_management.data.mapper.toDomain
import com.justappz.aniyomitv.extensions_management.data.mapper.toEntity
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepoDetailsRepository

class ExtensionRepoDetailsImpl(
    private val dao: ExtensionRepoDetailsDao,
) : ExtensionRepoDetailsRepository {


    override suspend fun getRepos(): BaseUiState<List<ExtensionRepositoriesDetailsDomain>> {
        return try {
            val repos = dao.getRepos().map { it.toDomain() }
            if (repos.isEmpty()) {
                BaseUiState.Empty
            } else {
                BaseUiState.Success(repos)
            }
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.UnknownError(
                    message = e.message ?: "Failed to load repositories",
                ),
            )
        }
    }

    override suspend fun addRepo(animeRepoDetail: ExtensionRepositoriesDetailsDomain): BaseUiState<Unit> {
        return try {
            dao.insertRepo(animeRepoDetail.toEntity())
            BaseUiState.Success(Unit)
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.RoomDbError(e.message ?: "Failed to insert repository"),
            )
        }
    }

}

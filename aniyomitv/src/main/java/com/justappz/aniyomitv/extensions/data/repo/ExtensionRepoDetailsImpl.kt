package com.justappz.aniyomitv.extensions.data.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.extensions.data.local.dao.ExtensionRepoDetailsDao
import com.justappz.aniyomitv.extensions.data.mapper.toDomain
import com.justappz.aniyomitv.extensions.data.mapper.toEntity
import com.justappz.aniyomitv.extensions.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions.domain.repo.ExtensionRepoDetailsRepository

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

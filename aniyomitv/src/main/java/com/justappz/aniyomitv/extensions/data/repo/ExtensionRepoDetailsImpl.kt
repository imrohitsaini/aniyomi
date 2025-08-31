package com.justappz.aniyomitv.extensions.data.repo

import android.util.Log
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.util.toObject
import com.justappz.aniyomitv.extensions.data.dto.ExtensionDTO
import com.justappz.aniyomitv.extensions.data.local.dao.ExtensionRepoDetailsDao
import com.justappz.aniyomitv.extensions.data.mapper.toDomain
import com.justappz.aniyomitv.extensions.data.mapper.toEntity
import com.justappz.aniyomitv.extensions.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions.domain.model.RepoDomain
import com.justappz.aniyomitv.extensions.domain.repo.ExtensionRepoDetailsRepository
import eu.kanade.tachiyomi.network.NetworkHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Request

class ExtensionRepoDetailsImpl(
    private val dao: ExtensionRepoDetailsDao, // From local db
    private val networkHelper: NetworkHelper, // From api
) : ExtensionRepoDetailsRepository {

    private val tag = "ExtensionRepoDetailsImpl"

    //region repos
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
    //endregion

    //region extensions
    override suspend fun loadExtensionsFromDB(url: String): Flow<BaseUiState<RepoDomain>> = flow {
        try {
            val entities = dao.getExtensionsByRepo(url)
            if (entities.isNotEmpty()) {
                emit(
                    BaseUiState.Success(
                        RepoDomain(
                            repoUrl = url,
                            extensions = entities.map { it.toDomain() }
                        )
                    )
                )
            }
        } catch (e: Exception) {
            emit(BaseUiState.Error(AppError.UnknownError(e.message ?: "DB error")))
        }
    }

    override suspend fun refreshExtensions(repoUrl: String): BaseUiState<Unit> {
        val client = networkHelper.client
        val request = Request.Builder()
            .url(repoUrl)
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return BaseUiState.Error(
                        AppError.ServerError(
                            code = response.code,
                            message = response.message.ifBlank { "HTTP error ${response.code}" },
                        ),
                    )
                }

                val body = response.body.string()
                val dtoList: List<ExtensionDTO> = body.toObject()

                if (dtoList.isEmpty()) return BaseUiState.Empty

                // Map to DB entities
                val extensionEntities = dtoList.map { it.toEntity(repoUrl) }


                dao.upsertExtensions(extensionEntities)

                BaseUiState.Success(Unit)
            }
        } catch (e: Exception) {
            BaseUiState.Error(AppError.NetworkError(e.message ?: "Network error"))
        }
    }

    //endregion

}

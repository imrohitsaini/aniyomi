package com.justappz.aniyomitv.database.repo

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.core.util.StringUtils.getAnimeKeyFromUrl
import com.justappz.aniyomitv.database.dao.AnimeEpisodeDao
import com.justappz.aniyomitv.database.mapper.toDomain
import com.justappz.aniyomitv.database.mapper.toEntity
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo

class DatabaseRepoImpl(
    private val dao: AnimeEpisodeDao,
) : AnimeEpisodeRepo {

    private val tag = "AnimeEpisodeRepoImpl"

    override suspend fun updateAnimeWithDb(animeDomain: AnimeDomain): BaseUiState<AnimeDomain> {
        return try {
            dao.insertAnime(animeDomain.toEntity())
            BaseUiState.Success(animeDomain)
        } catch (e: SQLiteConstraintException) {
            // Anime already exists → treat as success
            BaseUiState.Success(animeDomain)
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.RoomDbError(
                    message = "Anime insertion failed: ${e.message}",
                    displayType = ErrorDisplayType.TOAST,
                ),
            )
        }
    }

    override suspend fun updateEpisodeWithDb(episode: EpisodeDomain): BaseUiState<EpisodeDomain> {
        return try {
            val entities = dao.getEpisodesForAnime(episode.animeUrl.getAnimeKeyFromUrl())

            if (entities.isEmpty()) Log.d(tag, "No episodes for url ${episode.animeUrl}")

            dao.insertEpisode(episode.toEntity())
            BaseUiState.Success(episode)
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.RoomDbError(
                    message = "DB operation failed",
                    displayType = ErrorDisplayType.TOAST,
                ),
            )
        }
    }

    override suspend fun getAnimeWithKey(animeKey: String): BaseUiState<AnimeDomain?> {
        return try {
            val entity = dao.getAnimeWithKey(animeKey) // could be null
            BaseUiState.Success(entity?.toDomain())
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.RoomDbError(
                    message = "Fetch failed",
                    displayType = ErrorDisplayType.TOAST,
                ),
            )
        }
    }

    override suspend fun getAnimeInLibrary(): BaseUiState<List<AnimeDomain>> {
        return try {
            val entities = dao.getAnimeInLibrary()
            if (entities.isEmpty()) {
                BaseUiState.Empty
            } else {
                BaseUiState.Success(entities.map { it.toDomain() })
            }
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.RoomDbError(
                    message = "Fetch failed ${e.message}",
                    displayType = ErrorDisplayType.TOAST,
                ),
            )
        }
    }

    override suspend fun getAllEpisodesForAnime(animeKey: String): BaseUiState<List<EpisodeDomain>> {
        return try {
            val episodes = dao.getEpisodesForAnime(animeKey)
            if (episodes.isEmpty()) {
                BaseUiState.Empty
            } else {
                BaseUiState.Success(episodes.map { it.toDomain() })
            }
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.RoomDbError(
                    message = "Fetch failed ${e.message}",
                    displayType = ErrorDisplayType.TOAST,
                ),
            )
        }
    }
}

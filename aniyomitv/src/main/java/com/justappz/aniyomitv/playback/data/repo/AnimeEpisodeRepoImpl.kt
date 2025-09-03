package com.justappz.aniyomitv.playback.data.repo

import android.util.Log
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.playback.data.local.dao.AnimeEpisodeDao
import com.justappz.aniyomitv.playback.data.mapper.toDomain
import com.justappz.aniyomitv.playback.data.mapper.toEntity
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo

class AnimeEpisodeRepoImpl(
    private val dao: AnimeEpisodeDao,
) : AnimeEpisodeRepo {

    private val tag = "AnimeEpisodeRepoImpl"

    override suspend fun updateAnimeWithDb(animeDomain: AnimeDomain): BaseUiState<AnimeDomain> {
        return try {
            dao.insertAnime(animeDomain.toEntity())
            BaseUiState.Success(animeDomain)
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
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
            val entities = dao.getEpisodesForAnime(episode.animeUrl)

            if (entities.isEmpty()) Log.d(tag, "No episodes for url ${episode.animeUrl}")
            entities.forEachIndexed { index, item ->
                Log.d(tag, "$index  -> ${item.episodeKey}")
            }

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

}

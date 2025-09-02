package com.justappz.aniyomitv.playback.data.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.playback.data.local.dao.AnimeEpisodeDao
import com.justappz.aniyomitv.playback.data.mapper.toEntity
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo

class AnimeEpisodeRepoImpl(
    private val dao: AnimeEpisodeDao,
) : AnimeEpisodeRepo {

    override suspend fun updateAnimeWithDb(animeDomain: AnimeDomain): BaseUiState<AnimeDomain> {
        return try {
            dao.insertAnime(animeDomain.toEntity())
            BaseUiState.Success(animeDomain)
        } catch (e: Exception) {
            BaseUiState.Error(
                AppError.RoomDbError(
                    message = "Insertion failed",
                    displayType = ErrorDisplayType.TOAST,
                ),
            )
        }
    }
}

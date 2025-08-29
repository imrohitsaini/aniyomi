package com.justappz.aniyomitv.episodes.data.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.episodes.domain.repo.EpisodesRepository
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EpisodesRepoImpl : EpisodesRepository {

    override suspend fun getAnimeDetails(
        source: AnimeHttpSource,
        anime: SAnime,
    ): BaseUiState<SAnime> {
        return withContext(Dispatchers.IO) {
            try {
                val details = source.getAnimeDetails(anime)
                BaseUiState.Success(details)
            } catch (e: Exception) {
                // You can branch specific errors here if needed
                val appError = when (e) {
                    is java.net.UnknownHostException -> AppError.NetworkError("No Internet Connection")
                    is java.net.SocketTimeoutException -> AppError.NetworkError("Request Timeout")
                    else -> AppError.UnknownError(e.message ?: "Something went wrong")
                }
                BaseUiState.Error(appError)
            }
        }
    }

    override suspend fun getEpisodes(
        source: AnimeHttpSource,
        anime: SAnime,
    ): BaseUiState<List<SEpisode>> {
        return withContext(Dispatchers.IO) {
            try {
                val details = source.getEpisodeList(anime)
                BaseUiState.Success(details)
            } catch (e: Exception) {
                // You can branch specific errors here if needed
                val appError = when (e) {
                    is java.net.UnknownHostException -> AppError.NetworkError("No Internet Connection")
                    is java.net.SocketTimeoutException -> AppError.NetworkError("Request Timeout")
                    else -> AppError.UnknownError(e.message ?: "Something went wrong")
                }
                BaseUiState.Error(appError)
            }
        }
    }
}

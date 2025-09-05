package com.justappz.aniyomitv.episodes.data.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.episodes.domain.repo.EpisodesRepository
import com.justappz.aniyomitv.playback.data.local.dao.AnimeEpisodeDao
import com.justappz.aniyomitv.playback.data.mapper.toEntity
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class EpisodesRepoImpl(
    private val dao: AnimeEpisodeDao,
) : EpisodesRepository {

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
                    is UnknownHostException -> AppError.NetworkError("No Internet Connection")
                    is SocketTimeoutException -> AppError.NetworkError("Request Timeout")
                    else -> AppError.UnknownError(e.message ?: "Something went wrong")
                }
                BaseUiState.Error(appError)
            }
        }
    }

    override suspend fun getEpisodes(
        source: AnimeHttpSource,
        anime: SAnime,
    ): BaseUiState<List<EpisodeDomain>> {
        return withContext(Dispatchers.IO) {
            try {
                val remoteEpisodes = source.getEpisodeList(anime)

                if (remoteEpisodes.isEmpty()) {
                    return@withContext BaseUiState.Empty
                }

                // Get existing episodes from DB
                val localEpisodes = dao.getEpisodesForAnime(anime.url) // List<EpisodeEntity>

                // Merge remote + local
                val episodeDomains = remoteEpisodes.map { remote ->
                    val local = localEpisodes.find { it.url == remote.url }
                    EpisodeDomain(
                        url = remote.url,
                        name = remote.name,
                        dateUpload = remote.date_upload,
                        episodeNumber = remote.episode_number,
                        animeUrl = anime.url,
                        lastWatchTime = local?.lastWatchTime ?: 0L,
                        watchState = local?.watchState ?: 0,
                    )
                }

                dao.insertEpisodes(episodeDomains.map { it.toEntity() })

                if (episodeDomains.isEmpty()) {
                    BaseUiState.Empty
                } else {
                    BaseUiState.Success(episodeDomains)
                }
                BaseUiState.Success(episodeDomains)
            } catch (e: Exception) {
                // You can branch specific errors here if needed
                val appError = when (e) {
                    is UnknownHostException -> AppError.NetworkError("No Internet Connection")
                    is SocketTimeoutException -> AppError.NetworkError("Request Timeout")
                    else -> AppError.UnknownError(e.message ?: "Something went wrong")
                }
                BaseUiState.Error(appError)
            }
        }
    }

    override suspend fun getVideos(
        source: AnimeHttpSource,
        episode: SEpisode,
    ): BaseUiState<List<Video>> {
        return withContext(Dispatchers.IO) {
            try {
                val details = source.getVideoList(episode)
                if (details.isEmpty()) {
                    BaseUiState.Empty
                } else {
                    BaseUiState.Success(details)
                }
            } catch (e: Exception) {
                // You can branch specific errors here if needed
                val appError = when (e) {
                    is UnknownHostException -> AppError.NetworkError(
                        "No Internet Connection",
                        displayType = ErrorDisplayType.TOAST,
                    )

                    is SocketTimeoutException -> AppError.NetworkError(
                        "Request Timeout",
                        displayType = ErrorDisplayType.TOAST,
                    )

                    else -> AppError.UnknownError(
                        e.message ?: "Something went wrong",
                        displayType = ErrorDisplayType.TOAST,
                    )
                }
                BaseUiState.Error(appError)
            }
        }
    }
}

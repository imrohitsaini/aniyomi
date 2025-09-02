package com.justappz.aniyomitv.playback.domain.model

import eu.kanade.tachiyomi.animesource.model.SEpisode
import java.io.Serializable

data class EpisodeDomain(
    val url: String,           // unique episode id (auto-increment)
    val name: String,
    val dateUpload: Long,
    val episodeNumber: Float,

    // SAnime
    val animeUrl: String,      // foreign key → AnimeEntity.url

    // Playback tracking
    val lastWatchTime: Long = 0L, // in ms
    val watchState: Int = 0,       // 0 = not watched, 1 = in progress, 2 = completed
) : Serializable

fun getEpisodeDomain(animeUrl: String, lastWatchTime: Long, watchState: Int, episode: SEpisode): EpisodeDomain {
    return EpisodeDomain(
        url = episode.url,
        name = episode.name,
        dateUpload = episode.date_upload,
        episodeNumber = episode.episode_number,
        animeUrl = animeUrl,
        lastWatchTime = lastWatchTime,
        watchState = watchState,
    )
}

fun EpisodeDomain.toSEpisode(): SEpisode {
    return SEpisode.create().apply {
        url = this@toSEpisode.url
        name = this@toSEpisode.name
        date_upload = this@toSEpisode.dateUpload
        episode_number = this@toSEpisode.episodeNumber
        scanlator = null // or map it if you store scanlator in EpisodeDomain
    }
}

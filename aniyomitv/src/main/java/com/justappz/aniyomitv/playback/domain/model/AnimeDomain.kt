package com.justappz.aniyomitv.playback.domain.model

import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

data class AnimeDomain(
    // SANIME
    val url: String,
    val title: String,
    val artist: String?,
    val author: String?,
    val description: String?,
    val genre: String?,
    val status: Int,
    val thumbnailUrl: String?,

    // AnimeHttpSource
    val packageName: String,
    val className: String,
    val animeHttpSource: AnimeHttpSource? = null,
)

fun getAnimeDomain(packageName: String, className: String, anime: SAnime): AnimeDomain {
    return AnimeDomain(
        url = anime.url,
        title = anime.title,
        artist = anime.artist,
        author = anime.author,
        description = anime.description,
        genre = anime.genre,
        status = anime.status,
        thumbnailUrl = anime.thumbnail_url,
        packageName = packageName,
        className = className,
    )
}

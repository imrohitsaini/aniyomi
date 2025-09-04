package com.justappz.aniyomitv.playback.domain.model

import eu.kanade.tachiyomi.animesource.model.AnimeUpdateStrategy
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import java.io.Serializable

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

    val inLibrary: Boolean = false,
) : Serializable

fun getAnimeDomain(packageName: String, className: String, anime: SAnime, inLibrary: Boolean?): AnimeDomain {
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
        inLibrary = inLibrary == true,
    )
}

fun AnimeDomain.toSAnime(): SAnime {
    return SAnime.create().apply {
        url = this@toSAnime.url
        title = this@toSAnime.title
        artist = this@toSAnime.artist
        author = this@toSAnime.author
        description = this@toSAnime.description
        genre = this@toSAnime.genre
        status = this@toSAnime.status
        thumbnail_url = this@toSAnime.thumbnailUrl
        update_strategy = AnimeUpdateStrategy.ALWAYS_UPDATE // default, or load from AnimeDomain if you add it later
        initialized = true // assume domain objects are fully initialized
    }
}

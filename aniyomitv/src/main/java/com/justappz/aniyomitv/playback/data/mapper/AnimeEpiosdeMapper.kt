package com.justappz.aniyomitv.playback.data.mapper

import com.justappz.aniyomitv.core.util.StringUtils.getAnimeKeyFromUrl
import com.justappz.aniyomitv.playback.data.local.entity.AnimeEntity
import com.justappz.aniyomitv.playback.data.local.entity.EpisodeEntity
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain

fun AnimeDomain.toEntity(): AnimeEntity {
    return AnimeEntity(
        animeKey = url.getAnimeKeyFromUrl(),
        url = url,
        title = title,
        artist = artist ?: "",
        author = author ?: "",
        description = description ?: "",
        genre = genre ?: "",
        status = status,
        thumbnailUrl = thumbnailUrl ?: "",
        packageName = packageName,
        className = className,
        inLibrary = inLibrary,
    )
}

fun AnimeEntity.toDomain(): AnimeDomain {
    return AnimeDomain(
        url = url,
        title = title,
        artist = artist,
        author = author,
        description = description,
        genre = genre,
        status = status,
        thumbnailUrl = thumbnailUrl,
        packageName = packageName,
        className = className,
        inLibrary = inLibrary,
    )
}

fun EpisodeDomain.toEntity(): EpisodeEntity {
    return EpisodeEntity(
        episodeKey = "${animeUrl.getAnimeKeyFromUrl()}-$episodeNumber",
        url = url,
        name = name,
        dateUpload = dateUpload,
        episodeNumber = episodeNumber,
        scanlator = null,
        animeKey = animeUrl.getAnimeKeyFromUrl(),
        animeUrl = animeUrl,
        lastWatchTime = lastWatchTime,
        watchState = watchState,
    )
}

package com.justappz.aniyomitv.playback.data.mapper

import com.justappz.aniyomitv.playback.data.local.entity.AnimeEntity
import com.justappz.aniyomitv.playback.data.local.entity.EpisodeEntity
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain

fun AnimeDomain.toEntity(): AnimeEntity {
    return AnimeEntity(
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
    )
}

fun EpisodeDomain.toEntity(): EpisodeEntity {
    return EpisodeEntity(
        url = url,
        name = name,
        dateUpload = dateUpload,
        episodeNumber = episodeNumber,
        scanlator = null,
        animeUrl = animeUrl,
        lastWatchTime = lastWatchTime,
        watchState = watchState,
    )
}

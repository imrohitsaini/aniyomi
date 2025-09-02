package com.justappz.aniyomitv.playback.data.mapper

import com.justappz.aniyomitv.playback.data.local.entity.AnimeEntity
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain

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

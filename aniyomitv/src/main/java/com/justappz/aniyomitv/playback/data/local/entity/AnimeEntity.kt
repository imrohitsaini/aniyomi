package com.justappz.aniyomitv.playback.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.justappz.aniyomitv.constants.RoomDBConstants

@Entity(tableName = RoomDBConstants.ENTITY_ANIME)
data class AnimeEntity(
    @PrimaryKey
    val animeKey: String,

    // SANIME
    val url: String,
    val title: String,
    val artist: String,
    val author: String,
    val description: String,
    val genre: String,
    val status: Int,
    val thumbnailUrl: String,

    // AnimeHttpSource using ctx.loadAnimeSource(packageName, className)
    val packageName: String,
    val className: String,
)

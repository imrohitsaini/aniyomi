package com.justappz.aniyomitv.playback.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.justappz.aniyomitv.constants.RoomDBConstants

@Entity(
    tableName = RoomDBConstants.ENTITY_EPISODES,
    foreignKeys = [
        ForeignKey(
            entity = AnimeEntity::class,
            parentColumns = ["animeKey"],   // PK in AnimeEntity
            childColumns = ["animeKey"],    // FK in EpisodeEntity
            onDelete = ForeignKey.NO_ACTION,  // delete episodes if anime is deleted
        ),
    ],
)
data class EpisodeEntity(
    @PrimaryKey
    val episodeKey: String,
    val url: String,
    val name: String,
    val dateUpload: Long,
    val episodeNumber: Float,
    val scanlator: String?,
    val animeUrl: String,
    val animeKey: String, // FK

    // Playback tracking
    val lastWatchTime: Long = 0L,
    val watchState: Int = 0,
)

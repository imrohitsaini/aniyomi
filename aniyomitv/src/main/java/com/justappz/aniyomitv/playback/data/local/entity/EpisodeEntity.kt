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
            parentColumns = ["url"],        // PK in AnimeEntity
            childColumns = ["animeUrl"],    // FK in EpisodeEntity
            onDelete = ForeignKey.CASCADE,   // delete episodes if anime is deleted
        ),
    ],
    indices = [Index(value = ["animeUrl"])],
)
data class EpisodeEntity(
    // SEpisode
    @PrimaryKey
    val url: String,           // unique episode id (auto-increment)
    val name: String,
    val dateUpload: Long,
    val episodeNumber: Float,
    val scanlator: String?,

    // SAnime
    val animeUrl: String,      // foreign key → AnimeEntity.url

    // Playback tracking
    val lastWatchTime: Long = 0L, // in ms
    val watchState: Int = 0,       // 0 = not watched, 1 = in progress, 2 = completed
)

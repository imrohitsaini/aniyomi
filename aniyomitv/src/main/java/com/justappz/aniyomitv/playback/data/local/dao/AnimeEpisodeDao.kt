package com.justappz.aniyomitv.playback.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.justappz.aniyomitv.playback.data.local.entity.AnimeEntity
import com.justappz.aniyomitv.playback.data.local.entity.EpisodeEntity

@Dao
interface AnimeEpisodeDao {

    // Insert a new Anime (returns generated id)
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAnime(anime: AnimeEntity)

    // Insert or update a single episode
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity)
}
